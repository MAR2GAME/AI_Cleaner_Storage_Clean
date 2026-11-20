package com.aicleanerstorage.cleanerkit.clean.internal.scanner

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.internal.storage.DirectoryFileScanner
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateCandidate
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateFileGrouping
import com.aicleanerstorage.cleanerkit.internal.storage.FileContentDigest
import com.aicleanerstorage.cleanerkit.internal.storage.ScanDirectoryHelper.externalStorageDirectoryOrNull
import com.aicleanerstorage.cleanerkit.internal.storage.ScanDirectoryHelper.isReadableDirectory
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class DuplicateFileScanner : JunkScanner {
    override val category: JunkCategory = JunkCategory.DUPLICATE

    override suspend fun scan(): List<JunkFile> =
        withContext(Dispatchers.IO) {
            val files = scanPublicFilesFromFileSystem(coroutineContext::ensureActive)
            val hashes = mutableMapOf<String, String?>()

            val groups =
                buildDuplicateFileGroups(
                    files = files,
                    contentHash = { item ->
                        FileContentDigest.hashFile(
                            File(item.path),
                            maxBytes = MAX_HASH_FILE_SIZE_BYTES,
                            checkCancellation = coroutineContext::ensureActive,
                        ).also { hashes[item.path] = it }
                    },
                )

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

    private fun toJunkFile(file: ScannedFile): JunkFile =
        JunkFile(
            id = file.path.hashCode().toString(),
            filePath = file.path,
            fileName = file.name,
            fileSize = file.sizeBytes,
            category = JunkCategory.DUPLICATE,
            lastModified = file.modifiedMillis,
        )

    internal fun buildDuplicateFileGroups(
        files: List<ScannedFile>,
        contentHash: (ScannedFile) -> String?,
    ): List<List<ScannedFile>> =
        DuplicateFileGrouping.group(
            candidates = files.map { file ->
                DuplicateCandidate(
                    value = file,
                    identity = file.path,
                    name = file.name,
                    sizeBytes = file.sizeBytes,
                    modifiedMillis = file.modifiedMillis,
                )
            },
            contentHash = contentHash,
        )

    private fun scanPublicFilesFromFileSystem(checkCancellation: () -> Unit): List<ScannedFile> {
        val root = externalStorageDirectoryOrNull() ?: return emptyList()
        if (!root.isReadableDirectory()) return emptyList()

        return DirectoryFileScanner.scanPublicFiles(root, checkCancellation) { file, size ->
            ScannedFile(
                path = runCatching { file.canonicalPath }.getOrDefault(file.absolutePath),
                name = file.name,
                sizeBytes = size,
                modifiedMillis = runCatching { file.lastModified() }.getOrDefault(0L),
            )
        }
    }

    internal data class ScannedFile(
        val path: String,
        val name: String,
        val sizeBytes: Long,
        val modifiedMillis: Long,
    )

    private companion object {
        const val MAX_HASH_FILE_SIZE_BYTES = 100L * 1024 * 1024
    }
}
