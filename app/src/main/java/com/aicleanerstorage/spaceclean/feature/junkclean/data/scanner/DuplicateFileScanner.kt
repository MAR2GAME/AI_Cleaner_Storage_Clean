package com.aicleanerstorage.spaceclean.feature.junkclean.data.scanner

import com.aicleanerstorage.spaceclean.feature.junkclean.data.scanner.ScanDirectoryHelper.externalStorageDirectoryOrNull
import com.aicleanerstorage.spaceclean.feature.junkclean.data.scanner.ScanDirectoryHelper.isReadableDirectory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.common.files.DuplicateContentGuard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class DuplicateFileScanner : JunkScanner {
    override val category: JunkCategory = JunkCategory.DUPLICATE

    private var progress: Float = 0f

    override suspend fun scan(): List<JunkFile> =
        withContext(Dispatchers.IO) {
            progress = 0f
            val files = scanPublicFilesFromFileSystem(coroutineContext::ensureActive)
            progress = 35f
            val hashes = mutableMapOf<String, String?>()

            val groups =
                buildDuplicateFileGroups(
                    files = files,
                    contentHash = { item ->
                        DuplicateContentGuard.hashFile(
                            File(item.path),
                            maxBytes = MAX_HASH_FILE_SIZE_BYTES,
                            checkCancellation = coroutineContext::ensureActive,
                        ).also { hashes[item.path] = it }
                    },
                )
            progress = 100f

            groups
                .flatMap { group ->
                    val original = group.first()
                    group.drop(1).map { file ->
                        toJunkFile(file).copy(
                            duplicateHash = hashes.getValue(file.path),
                            duplicateOriginalPath = original.path,
                        )
                    }
                }
        }

    override fun getProgress(): Float = progress

    private fun toJunkFile(file: DuplicateCandidate): JunkFile =
        JunkFile(
            id = file.path.hashCode().toString(),
            filePath = file.path,
            fileName = file.name,
            fileSize = file.sizeBytes,
            category = JunkCategory.DUPLICATE,
            lastModified = file.modifiedSeconds,
        )

    internal fun buildDuplicateFileGroups(
        files: List<DuplicateCandidate>,
        contentHash: (DuplicateCandidate) -> String?,
    ): List<List<DuplicateCandidate>> {
        val uniqueFiles =
            files
                .filter { it.sizeBytes > 0L }
                .distinctBy { it.path }

        val hashGroups = mutableListOf<List<DuplicateCandidate>>()

        uniqueFiles
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .forEach { sameSizeFiles ->
                val hashed = sameSizeFiles.map { file -> file to contentHash(file) }
                hashed
                    .filter { !it.second.isNullOrBlank() }
                    .groupBy { it.second.orEmpty() }
                    .values
                    .map { group -> group.map { it.first } }
                    .filter { it.size > 1 }
                    .forEach { group ->
                        hashGroups += group.sortedByDescending { it.modifiedSeconds }
                    }
            }

        return hashGroups
            .distinctBy { group -> group.joinToString("|") { it.path } }
            .sortedByDescending(::duplicateReclaimableBytes)
    }

    private fun scanPublicFilesFromFileSystem(checkCancellation: () -> Unit): List<DuplicateCandidate> {
        val root = externalStorageDirectoryOrNull() ?: return emptyList()
        if (!root.isReadableDirectory()) return emptyList()

        val results = mutableListOf<DuplicateCandidate>()
        collectPublicFiles(root, results, maxDepth = 1, checkCancellation = checkCancellation)
        PUBLIC_SCAN_DIRECTORIES
            .map { File(root, it) }
            .filter { it.isReadableDirectory() }
            .distinctBy { runCatching { it.canonicalPath }.getOrDefault(it.absolutePath) }
            .forEach { directory -> collectPublicFiles(directory, results, maxDepth = 12, checkCancellation = checkCancellation) }

        return results.distinctBy { it.path }
    }

    private fun collectPublicFiles(
        directory: File,
        results: MutableList<DuplicateCandidate>,
        maxDepth: Int,
        checkCancellation: () -> Unit,
    ) {
        checkCancellation()
        if (maxDepth < 0 || results.size >= MAX_FILE_SYSTEM_RESULTS) return
        val children = runCatching { directory.listFiles() }.getOrNull().orEmpty()
        children.forEach { file ->
            checkCancellation()
            if (results.size >= MAX_FILE_SYSTEM_RESULTS) return
            if (shouldSkipPath(file.absolutePath, file.name)) return@forEach
            if (runCatching { file.isDirectory }.getOrDefault(false)) {
                collectPublicFiles(file, results, maxDepth - 1, checkCancellation)
            } else if (runCatching { file.isFile }.getOrDefault(false)) {
                val sizeBytes = runCatching { file.length() }.getOrDefault(0L)
                if (sizeBytes <= 0L) return@forEach
                results +=
                    DuplicateCandidate(
                        path = runCatching { file.canonicalPath }.getOrDefault(file.absolutePath),
                        name = file.name,
                        sizeBytes = sizeBytes,
                        modifiedSeconds = runCatching { file.lastModified() }.getOrDefault(0L),
                    )
            }
        }
    }

    private fun shouldSkipPath(
        path: String?,
        name: String,
    ): Boolean {
        val lowerName = name.lowercase(Locale.US)
        val lowerPath = path?.lowercase(Locale.US).orEmpty()
        if (lowerName.startsWith(".") || lowerName == "lost+found") return true
        if (lowerPath.isBlank()) return false

        val systemDirs =
            listOf(
                "/system",
                "/lost+found",
                "/preload",
                "/vendor",
                "/mnt",
                "/proc",
                "/sys",
                "/acct",
                "/dev",
                "/config",
                "/oem",
                "/firmware",
                "/cache",
            )
        return systemDirs.any { lowerPath == it || lowerPath.startsWith("$it/") }
    }

    private fun duplicateReclaimableBytes(group: List<DuplicateCandidate>): Long =
        group.sortedByDescending { it.modifiedSeconds }.drop(1).sumOf { it.sizeBytes }

    internal data class DuplicateCandidate(
        val path: String,
        val name: String,
        val sizeBytes: Long,
        val modifiedSeconds: Long,
    )

    private companion object {
        const val MAX_FILE_SYSTEM_RESULTS = 10_000
        const val MAX_HASH_FILE_SIZE_BYTES = 100L * 1024 * 1024  // 100 MB

        val PUBLIC_SCAN_DIRECTORIES =
            listOf(
                "Download",
                "Downloads",
                "Documents",
                "DCIM",
                "Pictures",
                "Movies",
                "Music",
                "WhatsApp",
                "Telegram",
                "Android/media",
            )
    }
}
