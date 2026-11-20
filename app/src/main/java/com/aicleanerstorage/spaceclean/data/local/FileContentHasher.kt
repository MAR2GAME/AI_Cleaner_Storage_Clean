package com.aicleanerstorage.spaceclean.data.local

import android.content.Context
import com.aicleanerstorage.spaceclean.base.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive

object FileContentHasher {
    fun hash(context: Context, item: ManagedFileItemModel, coroutineContext: CoroutineContext): String? =
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

data class DuplicateCandidate<T>(
    val value: T,
    val identity: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedSeconds: Long,
)

object DuplicateFileGrouping {
    fun <T> group(
        candidates: List<DuplicateCandidate<T>>,
        contentHash: (T) -> String?,
    ): List<List<T>> {
        val uniqueFiles = candidates.filter { it.sizeBytes > 0L }.distinctBy { it.identity }
        val hashGroups = mutableListOf<List<DuplicateCandidate<T>>>()

        uniqueFiles
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .forEach { sameSizeFiles ->
                val hashed = sameSizeFiles.map { file -> file to contentHash(file.value) }
                hashed
                    .filter { !it.second.isNullOrBlank() }
                    .groupBy { it.second.orEmpty() }
                    .values
                    .map { group -> group.map { it.first } }
                    .filter { it.size > 1 }
                    .forEach { group -> hashGroups += group.sortedByDescending { it.modifiedSeconds } }
            }

        return hashGroups
            .distinctBy { group -> group.joinToString("|") { it.identity } }
            .sortedByDescending { group -> group.sortedByDescending { it.modifiedSeconds }.drop(1).sumOf { it.sizeBytes } }
            .map { group -> group.map { it.value } }
    }
}
