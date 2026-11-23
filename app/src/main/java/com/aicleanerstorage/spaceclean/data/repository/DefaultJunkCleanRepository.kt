package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionRequester
import com.aicleanerstorage.spaceclean.data.catching
import com.aicleanerstorage.spaceclean.data.local.junkclean.JunkFileDeleter
import com.aicleanerstorage.spaceclean.data.local.junkclean.MemoryCleaner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.ApkScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.CacheScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.DuplicateFileScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.JunkScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.PublicJunkScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.TempFileScanner
import com.aicleanerstorage.spaceclean.data.local.storage.MediaDeletionAuthorization
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete
import com.aicleanerstorage.spaceclean.data.local.storage.authorizePendingMedia
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
import com.aicleanerstorage.spaceclean.domain.rule.JunkScanRule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

/**
 * 垃圾扫描/清理的唯一实现，直接实现 [JunkCleanRepository]。
 *
 * 负责协调多个 scanner、保存扫描会话状态和执行删除；不控制页面动画节奏，
 * 扫描页的最短展示时间由 presentation 的 effect 决定。
 *
 * @param context 用于扫描缓存/APK/文件和内存清理。
 * @param requester 系统删除授权契约；为 null 时登记待授权请求。
 * @param authorization 待授权请求的登记表。
 */
internal class DefaultJunkCleanRepository(
    private val context: Context,
    private val requester: MediaDeletionRequester?,
    private val authorization: MediaDeletionAuthorization,
) : JunkCleanRepository {
    private val cacheScanner = CacheScanner(context)
    private val tempFileScanner = TempFileScanner()
    private val apkScanner = ApkScanner()
    private val duplicateFileScanner = DuplicateFileScanner()
    private val memoryCleaner = MemoryCleaner

    /**
     * 扫描顺序：重复文件排在最前。
     *
     * 重复文件会决定哪些“原件”不能被清理（见 [JunkScanRule.normalize]）。先跑完
     * 重复文件，后续扫描器发现的同路径原件会被立即过滤，扫描过程中的累计大小
     * 只会增加；否则最后一步才发现原件，界面会出现“扫到 24MB 又跳回 20MB”。
     */
    private val scanners =
        listOf(
            duplicateFileScanner,
            cacheScanner,
            tempFileScanner,
            PublicJunkScanner(context),
            apkScanner,
        )

    private var lastScanFiles: List<JunkFileModel> = emptyList()

    private val work = Mutex()
    private val generation = AtomicLong()

    override suspend fun scan(onProgress: (JunkScanProgressModel) -> Unit): Result<JunkScanResultModel> =
        catching {
            withContext(Dispatchers.IO) {
                work.withLock { scanInternal(onProgress) }
            }
        }

    private suspend fun scanInternal(onProgress: (JunkScanProgressModel) -> Unit): JunkScanResultModel {
        generation.incrementAndGet()
        authorization.invalidate(MediaDeletionAuthorization.Owner.Clean)
        lastScanFiles = emptyList()
        val scanGeneration = generation.get()
        val allJunkFiles = mutableListOf<JunkFileModel>()
        var snapshot = JunkScanResultModel.EMPTY
        onProgress(snapshot.toScanProgress(percent = 0f))

        val weightPerScanner = 100f / scanners.size

        for ((index, scanner) in scanners.withIndex()) {
            onProgress(
                snapshot.toScanProgress(
                    percent = index * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )

            val files = scanSafely(scanner)
            allJunkFiles.addAll(files.map { it.copy(id = it.path) })

            snapshot = JunkScanResultModel(JunkScanRule.normalize(allJunkFiles))
            onProgress(
                snapshot.toScanProgress(
                    percent = (index + 1) * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )
        }

        onProgress(snapshot.toScanProgress(percent = 100f))
        if (scanGeneration != generation.get()) throw CancellationException("Scan session cleared")
        val result = snapshot
        lastScanFiles = result.files
        return result
    }

    override suspend fun cleanMemory(): Result<JunkMemoryCleanModel> =
        catching {
            withContext(Dispatchers.IO) {
                memoryCleaner.clean(context.applicationContext)
            }
        }

    override suspend fun deleteFiles(
        files: List<JunkFileModel>,
        shouldStop: () -> Boolean,
    ): Result<List<JunkDeleteOutcomeModel>> =
        catching {
            withContext(Dispatchers.IO) {
                work.withLock { deleteFilesLocked(files, shouldStop) }
            }
        }

    private suspend fun deleteFilesLocked(
        selectedFiles: List<JunkFileModel>,
        shouldStop: () -> Boolean,
    ): List<JunkDeleteOutcomeModel> {
        val coroutineContext = currentCoroutineContext()
        val version = generation.get()
        val known = lastScanFiles.associateBy { it.path }
        val paths = selectedFiles.map { it.path }.toSet()
        val automatic = mutableMapOf<String, PendingMediaDelete>()
        val completed = mutableListOf<JunkDeleteOutcomeModel>()
        return try {
            for (item in selectedFiles.distinctBy { it.path }) {
                coroutineContext.ensureActive()
                if (shouldStop() || version != generation.get()) break
                authorization.invalidate(MediaDeletionAuthorization.Owner.Clean, item.path)
                val outcome =
                    if (known[item.path] == item) {
                        JunkFileDeleter.delete(context, item, paths, coroutineContext::ensureActive) { media ->
                            if (requester != null) {
                                automatic[item.path] = media
                                JunkDeleteOutcomeModel(item, DeleteStatus.AuthorizationRequired)
                            } else {
                                val id =
                                    authorization.register(
                                        MediaDeletionAuthorization.Owner.Clean,
                                        item.path,
                                        media,
                                        current = { version == generation.get() },
                                    ) { status ->
                                        if (status == DeleteStatus.Deleted || status == DeleteStatus.AlreadyAbsent) {
                                            removeFromScanCache(listOf(item))
                                        }
                                    }
                                JunkDeleteOutcomeModel(item, DeleteStatus.AuthorizationRequired)
                            }
                        }
                    } else {
                        JunkDeleteOutcomeModel(item, DeleteStatus.Changed)
                    }
                completed += outcome
            }
            if (requester != null) {
                val requests = automatic.values.toList()
                val statuses =
                    authorizePendingMedia(context.contentResolver, requests, requester::request) {
                        shouldStop() || version != generation.get()
                    }
                for (index in completed.indices) {
                    val outcome = completed[index]
                    val uri = automatic[outcome.file.path]?.uri ?: continue
                    val status = statuses[uri] ?: DeleteStatus.Failed
                    completed[index] = outcome.copy(status = status)
                }
            }
            completed
        } finally {
            if (version == generation.get()) {
                removeFromScanCache(
                    completed.filter { it.deleted || it.status == DeleteStatus.AlreadyAbsent }.map { it.file },
                )
            }
        }
    }

    private fun removeFromScanCache(files: List<JunkFileModel>) {
        if (files.isEmpty()) return
        val removed = files.toHashSet()
        lastScanFiles = lastScanFiles.filterNot { it in removed }
    }

    private fun JunkScanResultModel.toScanProgress(
        percent: Float,
        currentCategory: JunkCategory? = null,
    ): JunkScanProgressModel =
        JunkScanProgressModel(
            percent = percent,
            currentCategory = currentCategory,
            foundCount = totalCount,
            foundSize = totalSize,
        )

    private suspend fun scanSafely(scanner: JunkScanner): List<JunkFileModel> =
        try {
            scanner.scan()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            emptyList()
        }
}
