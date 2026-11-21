package com.aicleanerstorage.cleanerkit.files.internal

import android.content.Context
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.internal.storage.FileContentDigest
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive

internal object FileContentHasher {
    fun hash(context: Context, item: ManagedFileItem, coroutineContext: CoroutineContext): String? =
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
