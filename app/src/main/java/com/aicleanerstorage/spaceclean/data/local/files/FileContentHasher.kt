package com.aicleanerstorage.spaceclean.data.local.files

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive

internal object FileContentHasher {
    fun hash(context: Context, item: ManagedFileModel, coroutineContext: CoroutineContext): String? =
        runCatching {
            coroutineContext.ensureActive()
            if (!item.path.isNullOrBlank() && File(item.path).isFile) {
                FileContentDigest.hashFile(File(item.path), checkCancellation = coroutineContext::ensureActive)
            } else {
                context.contentResolver.openInputStream(item.uri.toAndroidUri())
                    ?.use { sha256(it) { coroutineContext.ensureActive() } }
            }
        }.getOrElse {
            if (it is CancellationException) throw it
            coroutineContext.ensureActive()
            null
        }

    fun sha256(input: InputStream, checkCancellation: () -> Unit = {}): String =
        FileContentDigest.sha256(input, checkCancellation)
}
