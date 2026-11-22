package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.junkclean.JunkScanSessionStore

import android.content.Context
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionRequester
import com.aicleanerstorage.spaceclean.data.local.storage.MediaDeletionAuthorization
import com.aicleanerstorage.spaceclean.data.local.junkclean.JunkFileDeleter
import com.aicleanerstorage.spaceclean.data.local.junkclean.MemoryCleaner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.ApkScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.CacheScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.DuplicateFileScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.JunkScanner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.TempFileScanner
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete
import com.aicleanerstorage.spaceclean.data.local.storage.authorizePendingMedia
import com.aicleanerstorage.spaceclean.data.catching
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
import com.aicleanerstorage.spaceclean.domain.rule.JunkScanRule
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

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

    private val scanners =
        listOf(
            cacheScanner,
            tempFileScanner,
            apkScanner,
            duplicateFileScanner,
        )

    private val session = JunkScanSessionStore()

    override val scanProgress get() = session.scanProgress
    override val scanResult get() = session.scanResult

    private val work = Mutex()
    private val generation = AtomicLong()

    override fun clearSession() {
        synchronized(session) {
            generation.incrementAndGet()
            session.clear()
        }
        authorization.invalidate(MediaDeletionAuthorization.Owner.Clean)
    }

    override fun removeCleanedFiles(files: List<JunkFileModel>) =
        synchronized(session) { session.removeCleanedFiles(files) }

    override suspend fun scan(): Result<JunkScanResultModel> =
        catching {
            withContext(Dispatchers.IO) {
                work.withLock { scanInternal() }
            }
        }

    private suspend fun scanInternal(): JunkScanResultModel {
        clearSession()
        val scanGeneration = generation.get()
        val allJunkFiles = mutableListOf<JunkFileModel>()
        var snapshot = JunkScanResultModel.EMPTY
        session.setScanProgress(snapshot.toScanProgress(percent = 0f))

        val weightPerScanner = 100f / scanners.size

        for ((index, scanner) in scanners.withIndex()) {
            session.setScanProgress(
                snapshot.toScanProgress(
                    percent = index * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )

            val files = scanSafely(scanner)
            allJunkFiles.addAll(files.map { it.copy(id = it.path) })

            snapshot = JunkScanResultModel(JunkScanRule.normalize(allJunkFiles))
            session.setScanProgress(
                snapshot.toScanProgress(
                    percent = (index + 1) * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )
        }

        session.setScanProgress(snapshot.toScanProgress(percent = 100f))
        val result = snapshot
        synchronized(session) {
            if (scanGeneration != generation.get()) throw CancellationException("Scan session cleared")
            session.setScanResult(result)
        }
        return result
    }

    override suspend fun cleanMemory(): Result<JunkMemoryCleanModel> =
        catching {
            withContext(Dispatchers.IO) {
                JunkMemoryCleanModel(memoryCleaner.clean(context.applicationContext).freedBytes)
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
        val known = scanResult.value?.files.orEmpty().associateBy { it.path }
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
                                            removeCleanedFiles(listOf(item))
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
            synchronized(session) {
                if (version == generation.get()) {
                    session.removeCleanedFiles(
                        completed.filter { it.deleted || it.status == DeleteStatus.AlreadyAbsent }.map { it.file },
                    )
                }
            }
        }
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
