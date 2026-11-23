package com.aicleanerstorage.spaceclean.data.local.files

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

internal object FileContentHasher {
    fun hash(
        context: Context,
        item: ManagedFileModel,
        coroutineContext: CoroutineContext,
    ): String? =
        guarded(coroutineContext) {
            val path = item.path
            if (!path.isNullOrBlank() && File(path).isFile) {
                FileContentDigest.hashFile(File(path), checkCancellation = coroutineContext::ensureActive)
            } else {
                openStream(context, item)?.use { input ->
                    FileContentDigest.sha256(input) { coroutineContext.ensureActive() }
                }
            }
        }

    /**
     * 只读文件头部并算哈希。
     *
     * 用于在整文件哈希之前排除内容不同的同尺寸文件；拿到
     * [FileContentDigest.PrefixDigest.isComplete] 时调用方可以省掉一次全量读取。
     *
     * @param context 用于在路径不可读时改走 ContentResolver。
     * @param item 待读取文件。
     * @param coroutineContext 取消上下文。
     * @return 头部哈希；读取失败返回 null。
     */
    fun hashPrefix(
        context: Context,
        item: ManagedFileModel,
        coroutineContext: CoroutineContext,
    ): FileContentDigest.PrefixDigest? =
        guarded(coroutineContext) {
            val path = item.path
            if (!path.isNullOrBlank() && File(path).isFile) {
                FileContentDigest.hashFilePrefix(File(path), coroutineContext::ensureActive)
            } else {
                openStream(context, item)?.use { input ->
                    FileContentDigest.digestPrefix(input, FileContentDigest.PREFIX_HASH_BYTES) {
                        coroutineContext.ensureActive()
                    }
                }
            }
        }

    fun sha256(
        input: InputStream,
        checkCancellation: () -> Unit = {},
    ): String = FileContentDigest.sha256(input, checkCancellation)

    private fun openStream(
        context: Context,
        item: ManagedFileModel,
    ): InputStream? = context.contentResolver.openInputStream(item.uri.toAndroidUri())

    private inline fun <T> guarded(
        coroutineContext: CoroutineContext,
        block: () -> T?,
    ): T? =
        try {
            coroutineContext.ensureActive()
            block()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            null
        }
}
