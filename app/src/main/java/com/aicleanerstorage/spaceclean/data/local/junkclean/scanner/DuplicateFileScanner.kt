package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.data.local.storage.DirectoryFileScanner
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.externalStorageDirectoryOrNull
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.isReadableDirectory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDuplicateMetadataModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File

internal class DuplicateFileScanner : JunkScanner {
    override val category: JunkCategory = JunkCategory.DUPLICATE

    override suspend fun scan(): List<JunkFileModel> =
        withContext(Dispatchers.IO) {
            val checkCancellation = coroutineContext::ensureActive
            val files = scanPublicFilesFromFileSystem(checkCancellation)

            val groups =
                buildDuplicateFileGroups(
                    files = files,
                    prefixHash = { file -> hashPrefix(file, checkCancellation) },
                    contentHash = { file -> hashContent(file, checkCancellation) },
                )

            groups
                .flatMap { group ->
                    val original = group.files.first()
                    group.files.drop(1).map { file ->
                        toJunkFile(file).copy(
                            duplicate =
                                JunkDuplicateMetadataModel(
                                    hash = group.hash,
                                    originalPath = original.path,
                                ),
                        )
                    }
                }
        }

    private fun toJunkFile(file: ScannedFile): JunkFileModel =
        JunkFileModel(
            id = file.path.hashCode().toString(),
            path = file.path,
            name = file.name,
            sizeBytes = file.sizeBytes,
            category = JunkCategory.DUPLICATE,
            modifiedMillis = file.modifiedMillis,
        )

    internal fun buildDuplicateFileGroups(
        files: List<ScannedFile>,
        prefixHash: (ScannedFile) -> String?,
        contentHash: (ScannedFile) -> String?,
    ): List<DuplicateFileRule.Group<ScannedFile>> =
        DuplicateFileRule.group(
            candidates =
                files.map { file ->
                    DuplicateFileRule.Candidate(
                        value = file,
                        identity = file.path,
                        name = file.name,
                        sizeBytes = file.sizeBytes,
                        modifiedMillis = file.modifiedMillis,
                    )
                },
            prefixHash = prefixHash,
            contentHash = contentHash,
        )

    private fun hashPrefix(
        file: ScannedFile,
        checkCancellation: () -> Unit,
    ): String? = FileContentDigest.hashFilePrefix(File(file.path), checkCancellation)?.hash

    private fun hashContent(
        file: ScannedFile,
        checkCancellation: () -> Unit,
    ): String? = FileContentDigest.hashFile(File(file.path), MAX_HASH_FILE_SIZE_BYTES, checkCancellation)

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
