package com.aicleanerstorage.spaceclean.data.local.junkclean

import android.app.RecoverableSecurityException
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.data.local.storage.inspectFile
import com.aicleanerstorage.spaceclean.data.local.storage.unlinkFile
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete
import com.aicleanerstorage.spaceclean.data.local.storage.inspectMedia
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import java.io.File

/**
 * 鍗曚釜鍨冨溇鏂囦欢鐨勫垹闄ゅ櫒銆?
 *
 * 璐熻矗鍒犻櫎鍓嶅鏍搞€侀噸澶嶆枃浠朵繚鐣欐牎楠屻€佺洿鎺ュ垹闄ゆ垨鐧昏 MediaStore 鎺堟潈锛?
 * 涓嶈礋璐ｆ壒閲忔眹鎬诲拰椤甸潰鐘舵€併€?
 */
internal object JunkFileDeleter {
    /**
     * 鍒犻櫎涓€涓瀮鍦炬枃浠躲€?
     *
     * @param context 鐢ㄤ簬璁块棶 MediaStore 鍜岀郴缁熷垹闄ゆ巿鏉冦€?
     * @param junkFile 寰呭垹闄ゆ枃浠躲€?
     * @param selectedPaths 鏈鎵归噺鍒犻櫎涓凡閫変腑鐨勮矾寰勶紝鐢ㄤ簬閲嶅鏂囦欢淇濈暀鏍￠獙銆?
     * @param checkCancellation 鍙栨秷妫€鏌ワ紱鍒犻櫎鍓嶅拰鎺堟潈鍥炶皟鍚庨兘浼氳皟鐢ㄣ€?
     * @param defer 闇€瑕佺郴缁熸巿鏉冩椂鐨勫洖璋冿紱杩斿洖寰呮巿鏉冪粨鏋溿€?
     * @return 鍗曟枃浠跺垹闄ょ粨鏋溿€?
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) defer(expected)
            else result(DeleteStatus.Failed)
        } catch (_: Exception) {
            result(DeleteStatus.Unknown)
        }
    }

    /**
     * 鍒ゆ柇閲嶅鏂囦欢鏄惁鍙互鍒犻櫎銆?
     *
     * @param junkFile 寰呭垹闄ゆ枃浠讹紱蹇呴』甯?`duplicateHash` 鍜?`duplicateOriginalPath`銆?
     * @param selectedPaths 鏈鎵归噺鍒犻櫎涓凡閫変腑鐨勮矾寰勩€?
     * @param checkCancellation 鍝堝笇璇诲彇杩囩▼涓殑鍙栨秷妫€鏌ャ€?
     * @return 浠嶅瓨鍦ㄦ湭閫変腑鐨勩€佸唴瀹逛竴鑷寸殑鍘熷鏂囦欢鏃惰繑鍥?true銆?
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
        ) return false
        return DuplicateFileRule.hasVerifiedCopy(
            candidate = File(junkFile.path),
            retainedCandidates = listOf(original),
            expectedHash = junkFile.duplicate?.hash,
            identity = { it.canonicalPath },
            contentHash = { FileContentDigest.hashFile(it, checkCancellation = checkCancellation) },
        )
    }

    /**
     * 鍦?MediaStore 涓煡鎵炬枃浠跺搴旂殑 URI銆?
     *
     * @param contentResolver 鐢ㄤ簬鏌ヨ MediaStore銆?
     * @param file 鏈湴鏂囦欢銆?
     * @return 鎵惧埌鏃惰繑鍥?content URI锛屽惁鍒欒繑鍥?null銆?
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

