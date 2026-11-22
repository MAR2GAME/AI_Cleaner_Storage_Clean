package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDuplicateMetadataModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.data.local.storage.DirectoryFileScanner
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.externalStorageDirectoryOrNull
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.isReadableDirectory
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class DuplicateFileScanner : JunkScanner {
    override val category: JunkCategory = JunkCategory.DUPLICATE

    override suspend fun scan(): List<JunkFileModel> =
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
                            duplicate =
                                JunkDuplicateMetadataModel(
                                    hash = hashes.getValue(file.path),
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
        contentHash: (ScannedFile) -> String?,
    ): List<List<ScannedFile>> =
        DuplicateFileRule.group(
            candidates = files.map { file ->
                DuplicateFileRule.Candidate(
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

