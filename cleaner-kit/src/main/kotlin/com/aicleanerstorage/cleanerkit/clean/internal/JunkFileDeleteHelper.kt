package com.aicleanerstorage.cleanerkit.clean.internal

import android.app.RecoverableSecurityException
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.internal.storage.inspectFile
import com.aicleanerstorage.cleanerkit.internal.storage.unlinkFile
import com.aicleanerstorage.cleanerkit.internal.storage.PendingMediaDelete
import com.aicleanerstorage.cleanerkit.internal.storage.inspectMedia
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateContentRule
import com.aicleanerstorage.cleanerkit.internal.storage.FileContentDigest
import java.io.File

internal object JunkFileDeleteHelper {
    fun delete(
        context: Context,
        junkFile: JunkFile,
        selectedPaths: Set<String> = setOf(junkFile.filePath),
        checkCancellation: () -> Unit,
        defer: (PendingMediaDelete) -> JunkDeleteOutcome,
    ): JunkDeleteOutcome {
        checkCancellation()
        val file = File(junkFile.filePath)
        val duplicate = junkFile.category == JunkCategory.DUPLICATE
        fun result(status: DeleteStatus) = JunkDeleteOutcome(junkFile, status)
        inspectFile(file, junkFile.fileSize, junkFile.lastModified)?.let { return result(it) }
        if (duplicate && !canDeleteDuplicate(junkFile, selectedPaths, checkCancellation)) return result(DeleteStatus.Changed)
        checkCancellation()
        inspectFile(file, junkFile.fileSize, junkFile.lastModified)?.let { return result(it) }
        if (unlinkFile(file)) return result(DeleteStatus.Deleted)
        if (duplicate) return result(DeleteStatus.Failed)
        val resolver = context.contentResolver
        val uri = findMediaUri(resolver, file) ?: return result(DeleteStatus.Failed)
        val expected = PendingMediaDelete(uri, junkFile.fileSize, junkFile.lastModified / 1000L, null)
        inspectMedia(resolver, expected)?.let { return result(it) }
        return try {
            if (resolver.delete(uri, null, null) > 0) result(DeleteStatus.Deleted) else result(DeleteStatus.Unknown)
        } catch (error: RecoverableSecurityException) {
            defer(expected.copy(recovery = error.userAction.actionIntent.intentSender))
        } catch (_: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) defer(expected)
            else result(DeleteStatus.Failed)
        } catch (_: Exception) {
            result(DeleteStatus.Unknown)
        }
    }

    internal fun canDeleteDuplicate(
        junkFile: JunkFile,
        selectedPaths: Set<String>,
        checkCancellation: () -> Unit = {},
    ): Boolean {
        val original = junkFile.duplicateOriginalPath?.let(::File) ?: return false
        val originalPath = runCatching { original.canonicalPath }.getOrNull() ?: return false
        if (selectedPaths.any { selected ->
                val path = runCatching { File(selected).canonicalPath }.getOrNull() ?: return false
                originalPath == path || originalPath.startsWith(path.trimEnd(File.separatorChar) + File.separator)
            }
        ) return false
        return DuplicateContentRule.hasVerifiedCopy(
            candidate = File(junkFile.filePath),
            retainedCandidates = listOf(original),
            expectedHash = junkFile.duplicateHash,
            identity = { it.canonicalPath },
            contentHash = { FileContentDigest.hashFile(it, checkCancellation = checkCancellation) },
        )
    }

    fun findMediaUri(
        contentResolver: ContentResolver,
        file: File,
    ): Uri? {
        val path = file.absolutePath
        val collections =
            buildList {
                add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                add(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                add(MediaStore.Files.getContentUri("external"))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(MediaStore.Downloads.EXTERNAL_CONTENT_URI)
                }
            }

        for (collection in collections) {
            val uri = queryMediaUri(contentResolver, collection, path)
            if (uri != null) return uri
        }
        return null
    }

    private fun queryMediaUri(
        contentResolver: ContentResolver,
        collection: Uri,
        path: String,
    ): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"

        return runCatching {
            contentResolver.query(collection, projection, selection, arrayOf(path), null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    Uri.withAppendedPath(collection, id.toString())
                } else {
                    null
                }
            }
        }.getOrNull()
    }

}
