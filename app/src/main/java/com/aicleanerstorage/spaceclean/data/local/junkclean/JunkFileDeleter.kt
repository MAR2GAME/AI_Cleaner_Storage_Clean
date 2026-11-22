package com.aicleanerstorage.spaceclean.data.local.junkclean

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete
import com.aicleanerstorage.spaceclean.data.local.storage.inspectFile
import com.aicleanerstorage.spaceclean.data.local.storage.inspectMedia
import com.aicleanerstorage.spaceclean.data.local.storage.unlinkFile
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import java.io.File

/**
 * 单个垃圾文件的删除器。
 *
 * 负责删除前复核、重复文件保留校验、直接删除或登记 MediaStore 授权；
 * 不负责批量汇总和页面状态。
 */
internal object JunkFileDeleter {
    /**
     * 删除一个垃圾文件。
     *
     * @param context 用于访问 MediaStore 和系统删除授权。
     * @param junkFile 待删除文件。
     * @param selectedPaths 本次批量删除中已选中的路径，用于重复文件保留校验。
     * @param checkCancellation 取消检查；删除前和授权回调后都会调用。
     * @param defer 需要系统授权时的回调；返回待授权结果。
     * @return 单文件删除结果。
     */
    fun delete(
        context: Context,
        junkFile: JunkFileModel,
        selectedPaths: Set<String> = setOf(junkFile.path),
        checkCancellation: () -> Unit,
        defer: (PendingMediaDelete) -> JunkDeleteOutcomeModel,
    ): JunkDeleteOutcomeModel {
        checkCancellation()
        val file = File(junkFile.path)
        val duplicate = junkFile.category == JunkCategory.DUPLICATE

        fun result(status: DeleteStatus) = JunkDeleteOutcomeModel(junkFile, status)
        inspectFile(file, junkFile.sizeBytes, junkFile.modifiedMillis)?.let { return result(it) }
        if (duplicate && !canDeleteDuplicate(junkFile, selectedPaths, checkCancellation)) return result(DeleteStatus.Changed)
        checkCancellation()
        inspectFile(file, junkFile.sizeBytes, junkFile.modifiedMillis)?.let { return result(it) }
        if (unlinkFile(file)) return result(DeleteStatus.Deleted)
        if (duplicate) return result(DeleteStatus.Failed)
        val resolver = context.contentResolver
        val uri = findMediaUri(resolver, file) ?: return result(DeleteStatus.Failed)
        val expected = PendingMediaDelete(uri, junkFile.sizeBytes, junkFile.modifiedMillis / 1000L, null)
        inspectMedia(resolver, expected)?.let { return result(it) }
        return try {
            if (resolver.delete(uri, null, null) > 0) result(DeleteStatus.Deleted) else result(DeleteStatus.Unknown)
        } catch (error: RecoverableSecurityException) {
            defer(expected.copy(recovery = error.userAction.actionIntent.intentSender))
        } catch (_: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                defer(expected)
            } else {
                result(DeleteStatus.Failed)
            }
        } catch (_: Exception) {
            result(DeleteStatus.Unknown)
        }
    }

    /**
     * 判断重复文件是否可以删除。
     *
     * @param junkFile 待删除文件；必须带 `duplicateHash` 和 `duplicateOriginalPath`。
     * @param selectedPaths 本次批量删除中已选中的路径。
     * @param checkCancellation 哈希读取过程中的取消检查。
     * @return 仍存在未选中的、内容一致的原始文件时返回 true。
     */
    internal fun canDeleteDuplicate(
        junkFile: JunkFileModel,
        selectedPaths: Set<String>,
        checkCancellation: () -> Unit = {},
    ): Boolean {
        val original = junkFile.duplicate?.originalPath?.let(::File) ?: return false
        val originalPath = runCatching { original.canonicalPath }.getOrNull() ?: return false
        if (selectedPaths.any { selected ->
                val path = runCatching { File(selected).canonicalPath }.getOrNull() ?: return false
                originalPath == path || originalPath.startsWith(path.trimEnd(File.separatorChar) + File.separator)
            }
        ) {
            return false
        }
        return DuplicateFileRule.hasVerifiedCopy(
            candidate = File(junkFile.path),
            retainedCandidates = listOf(original),
            expectedHash = junkFile.duplicate?.hash,
            identity = { it.canonicalPath },
            contentHash = { FileContentDigest.hashFile(it, checkCancellation = checkCancellation) },
        )
    }

    /**
     * 在 MediaStore 中查找文件对应的 URI。
     *
     * @param contentResolver 用于查询 MediaStore。
     * @param file 本地文件。
     * @return 找到时返回 content URI，否则返回 null。
     */
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
