package com.aicleanerstorage.cleanerkit.clean.internal

import com.aicleanerstorage.cleanerkit.MediaDeletionAuthorization

import android.content.Context
import com.aicleanerstorage.cleanerkit.internal.storage.PendingMediaDelete
import com.aicleanerstorage.cleanerkit.internal.storage.authorizePendingMedia
import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.cleanerkit.clean.internal.scanner.ApkScanner
import com.aicleanerstorage.cleanerkit.clean.internal.scanner.CacheScanner
import com.aicleanerstorage.cleanerkit.clean.internal.scanner.DuplicateFileScanner
import com.aicleanerstorage.cleanerkit.clean.internal.scanner.JunkScanner
import com.aicleanerstorage.cleanerkit.clean.internal.scanner.TempFileScanner
import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.MemoryCleanResult
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class CleanRepositoryImpl(
    private val context: Context,
    private val authorize: (suspend (android.content.IntentSender) -> Boolean)?,
    private val authorization: MediaDeletionAuthorization,
) : CleanRepository {
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

    private val sharedState = InMemoryCleanSessionStore()
    override val scanProgress get() = sharedState.scanProgress
    override val scanResult get() = sharedState.scanResult

    private val work = Mutex()
    private val generation = AtomicLong()

    override fun clearSession() {
        synchronized(sharedState) {
            generation.incrementAndGet()
            sharedState.clear()
        }
        authorization.invalidate(MediaDeletionAuthorization.Owner.Clean)
    }

    override fun removeCleanedFiles(files: List<JunkFile>) = synchronized(sharedState) { sharedState.removeCleanedFiles(files) }

    override suspend fun performFullScan(): ScanResult =
        withContext(Dispatchers.IO) {
            work.withLock { scan() }
        }

    private suspend fun scan(): ScanResult {
        clearSession()
        val scanGeneration = generation.get()
        val scanId = UUID.randomUUID().toString()
        val allJunkFiles = mutableListOf<JunkFile>()
        var snapshot = ScanResult.EMPTY
        sharedState.setScanProgress(snapshot.toScanProgress(percent = 0f))

        val weightPerScanner = 100f / scanners.size

        for ((index, scanner) in scanners.withIndex()) {
            sharedState.setScanProgress(
                snapshot.toScanProgress(
                    percent = index * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )
            delay(150)

            val files = scanSafely(scanner)
            allJunkFiles.addAll(files.map { it.copy(id = it.filePath, scanId = scanId) })

            snapshot = ScanResult(dedupeJunkFiles(allJunkFiles))
            sharedState.setScanProgress(
                snapshot.toScanProgress(
                    percent = (index + 1) * weightPerScanner,
                    currentCategory = scanner.category,
                ),
            )
        }

        sharedState.setScanProgress(snapshot.toScanProgress(percent = 100f))

        val result =
            snapshot.copy(
                sharedStorageAccessLimited = if (android.os.Build.VERSION.SDK_INT >= 30) {
                    !android.os.Environment.isExternalStorageManager()
                } else context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED,
            )
        synchronized(sharedState) {
            if (scanGeneration != generation.get()) throw CancellationException("Scan session cleared")
            sharedState.setScanResult(result)
        }
        return result
    }

    override suspend fun cleanMemory(): MemoryCleanResult =
        withContext(Dispatchers.IO) {
            memoryCleaner.clean(context.applicationContext)
        }

    override suspend fun deleteFileItems(selectedFiles: List<JunkFile>): List<JunkDeleteOutcome> =
        deleteFileItems(selectedFiles) { false }

    override suspend fun deleteFileItems(
        selectedFiles: List<JunkFile>,
        shouldStop: () -> Boolean,
    ): List<JunkDeleteOutcome> = withContext(Dispatchers.IO) {
        work.withLock {
            val version = generation.get()
            val known = scanResult.value?.junkFiles.orEmpty().associateBy { it.filePath }
            val paths = selectedFiles.map { it.filePath }.toSet()
            val automatic = mutableMapOf<String, PendingMediaDelete>()
            val completed = mutableListOf<JunkDeleteOutcome>()
            try {
                for (item in selectedFiles.distinctBy { it.filePath }) {
                    coroutineContext.ensureActive()
                    if (shouldStop() || version != generation.get()) break
                    authorization.invalidate(MediaDeletionAuthorization.Owner.Clean, item.filePath)
                    val outcome = if (known[item.filePath] == item) {
                        JunkFileDeleteHelper.delete(context, item, paths, coroutineContext::ensureActive) { media ->
                            if (authorize != null) {
                                automatic[item.filePath] = media
                                JunkDeleteOutcome(item, DeleteStatus.AuthorizationRequired)
                            } else {
                                val id = authorization.register(
                                    MediaDeletionAuthorization.Owner.Clean, item.filePath, media,
                                    current = { version == generation.get() },
                                ) { status ->
                                    if (status == DeleteStatus.Deleted || status == DeleteStatus.AlreadyAbsent) {
                                        removeCleanedFiles(listOf(item))
                                    }
                                }
                                JunkDeleteOutcome(item, DeleteStatus.AuthorizationRequired, authorizationId = id)
                            }
                        }
                    } else {
                        JunkDeleteOutcome(item, DeleteStatus.Changed)
                    }
                    completed += outcome
                }
                if (authorize != null) {
                    val requests = automatic.values.toList()
                    val statuses = authorizePendingMedia(context.contentResolver, requests, authorize) { shouldStop() || version != generation.get() }
                    for (index in completed.indices) {
                        val outcome = completed[index]
                        val uri = automatic[outcome.junkFile.filePath]?.uri ?: continue
                        val status = statuses[uri] ?: DeleteStatus.Failed
                        completed[index] = JunkDeleteOutcome(outcome.junkFile, status)
                    }
                }
                completed
            } finally {
                synchronized(sharedState) {
                    if (version == generation.get()) {
                        sharedState.removeCleanedFiles(completed.filter { it.deleted || it.status == DeleteStatus.AlreadyAbsent }.map { it.junkFile })
                    }
                }
            }
        }
    }

    private fun ScanResult.toScanProgress(
        percent: Float,
        currentCategory: JunkCategory? = null,
    ): ScanProgress =
        ScanProgress(
            percent = percent,
            currentCategory = currentCategory,
            foundCount = totalCount,
            foundSize = totalSize,
        )

    private fun dedupeJunkFiles(files: List<JunkFile>): List<JunkFile> =
        files.sortedBy { if (it.category == JunkCategory.DUPLICATE) 0 else 1 }.distinctBy { it.filePath }

    private suspend fun scanSafely(scanner: JunkScanner): List<JunkFile> =
        try {
            scanner.scan()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            emptyList()
        }
}
