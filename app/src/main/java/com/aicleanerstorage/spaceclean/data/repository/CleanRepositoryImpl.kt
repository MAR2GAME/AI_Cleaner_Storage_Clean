package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.AdJunkScanner
import com.aicleanerstorage.spaceclean.data.local.ApkScanner
import com.aicleanerstorage.spaceclean.data.local.CacheScanner
import com.aicleanerstorage.spaceclean.data.local.DuplicateFileScanner
import com.aicleanerstorage.spaceclean.data.local.JunkFileDeleteHelper
import com.aicleanerstorage.spaceclean.data.local.JunkScanner
import com.aicleanerstorage.spaceclean.data.local.MemoryCleaner
import com.aicleanerstorage.spaceclean.data.local.ResidualScanner
import com.aicleanerstorage.spaceclean.data.local.ScanDirectoryHelper
import com.aicleanerstorage.spaceclean.data.local.TempFileScanner
import com.aicleanerstorage.spaceclean.data.local.InMemoryCleanSessionStore
import com.aicleanerstorage.spaceclean.domain.model.CleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryCleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.ScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import com.aicleanerstorage.spaceclean.domain.port.CleanRepository
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class CleanRepositoryImpl(
    private val context: Context,
) : CleanRepository {
    private data class DedupedScanSnapshot(
        val junkFiles: List<JunkFileModel>,
        val totalSize: Long,
        val totalCount: Int,
        val categoryBreakdown: Map<JunkCategory, List<JunkFileModel>>,
    )

    private val cacheScanner = CacheScanner(context)
    private val tempFileScanner = TempFileScanner()
    private val apkScanner = ApkScanner()
    private val residualScanner = ResidualScanner(context)
    private val adJunkScanner = AdJunkScanner(context)
    private val duplicateFileScanner = DuplicateFileScanner()
    private val memoryCleaner = MemoryCleaner

    private val scanners =
        listOf(
            cacheScanner,
            tempFileScanner,
            residualScanner,
            apkScanner,
            adJunkScanner,
            duplicateFileScanner,
        )

    private val sharedState = InMemoryCleanSessionStore()
    override val scanProgress get() = sharedState.scanProgress
    override val scanResult get() = sharedState.scanResult

    override fun clearSession() = sharedState.clear()

    override fun removeCleanedFiles(files: List<JunkFileModel>) = sharedState.removeCleanedFiles(files)

    override suspend fun performFullScan(): ScanResultModel =
        withContext(Dispatchers.IO) {
            ScanDirectoryHelper.clearCache()
            val allJunkFiles = mutableListOf<JunkFileModel>()
            sharedState.setScanProgress(buildDedupedScanSnapshot(allJunkFiles).toScanProgress(percent = 0f))

            val weightPerScanner = 100f / scanners.size

            for ((index, scanner) in scanners.withIndex()) {
                val beforeScanSnapshot = buildDedupedScanSnapshot(allJunkFiles)
                sharedState.setScanProgress(
                    beforeScanSnapshot.toScanProgress(
                        percent = index * weightPerScanner,
                        currentCategory = scanner.category,
                    ),
                )
                delay(150)

                val files = scanSafely(scanner)
                allJunkFiles.addAll(files)

                val afterScanSnapshot = buildDedupedScanSnapshot(allJunkFiles)
                sharedState.setScanProgress(
                    afterScanSnapshot.toScanProgress(
                        percent = (index + 1) * weightPerScanner,
                        currentCategory = scanner.category,
                    ),
                )
            }

            val finalSnapshot = buildDedupedScanSnapshot(allJunkFiles)
            sharedState.setScanProgress(finalSnapshot.toScanProgress(percent = 100f))

            val result =
                ScanResultModel(
                    junkFiles = finalSnapshot.junkFiles,
                    totalSize = finalSnapshot.totalSize,
                    totalCount = finalSnapshot.totalCount,
                    categoryBreakdown = finalSnapshot.categoryBreakdown,
                )
            sharedState.setScanResult(result)
            result
        }

    override suspend fun cleanMemory(): MemoryCleanResultModel =
        withContext(Dispatchers.IO) {
            memoryCleaner.clean(context.applicationContext)
        }

    override suspend fun deleteFileItems(selectedFiles: List<JunkFileModel>): List<JunkDeleteOutcomeModel> =
        withContext(Dispatchers.IO) {
            val selectedPaths = selectedFiles.map { it.filePath }.toSet()
            selectedFiles.map { JunkFileDeleteHelper.delete(context, it, selectedPaths, coroutineContext::ensureActive) }
        }

    override suspend fun finalizeAuthorizedDeletes(pendingOutcomes: List<JunkDeleteOutcomeModel>): CleanResultModel =
        withContext(Dispatchers.IO) {
            JunkFileDeleteHelper.finalizeAuthorizedDeletes(pendingOutcomes)
        }

    private fun buildDedupedScanSnapshot(files: List<JunkFileModel>): DedupedScanSnapshot {
        val uniqueJunkFiles = dedupeJunkFiles(files)
        return DedupedScanSnapshot(
            junkFiles = uniqueJunkFiles,
            totalSize = uniqueJunkFiles.sumOf { it.fileSize },
            totalCount = uniqueJunkFiles.size,
            categoryBreakdown = uniqueJunkFiles.groupBy { it.category },
        )
    }

    private fun DedupedScanSnapshot.toScanProgress(
        percent: Float,
        currentCategory: JunkCategory? = null,
    ): ScanProgressModel =
        ScanProgressModel(
            percent = percent,
            currentCategory = currentCategory,
            foundCount = totalCount,
            foundSize = totalSize,
        )

    private fun dedupeJunkFiles(files: List<JunkFileModel>): List<JunkFileModel> {
        val sorted =
            files.sortedWith(
                compareBy<JunkFileModel> { normalizedPath(it.filePath).length }
                    .thenBy { it.category.ordinal },
            )
        val kept = mutableListOf<JunkFileModel>()
        val keptPaths = mutableSetOf<String>()

        for (file in sorted) {
            val path = normalizedPath(file.filePath)
            if (path in keptPaths) continue
            if (keptPaths.any { parent -> path != parent && path.startsWith("$parent/") }) continue

            kept += file
            keptPaths += path
        }

        return kept
    }

    private fun normalizedPath(path: String): String =
        runCatching { File(path).canonicalPath }
            .getOrElse { path }
            .replace('\\', '/')
            .trimEnd('/')

    private suspend fun scanSafely(scanner: JunkScanner): List<JunkFileModel> =
        try {
            scanner.scan()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            emptyList()
        }
}
