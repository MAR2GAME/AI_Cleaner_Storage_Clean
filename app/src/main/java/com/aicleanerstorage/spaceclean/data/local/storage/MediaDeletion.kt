package com.aicleanerstorage.spaceclean.data.local.storage

import android.content.ContentResolver
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal data class PendingMediaDelete(
    val uri: Uri,
    val size: Long,
    val modifiedSeconds: Long,
    val recovery: IntentSender?,
)

internal fun inspectMedia(
    resolver: ContentResolver,
    item: PendingMediaDelete,
): DeleteStatus? {
    val projection = arrayOf(MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_MODIFIED)
    return try {
        val cursor = resolver.query(item.uri, projection, null, null, null) ?: return DeleteStatus.Unknown
        cursor.use {
            when {
                !it.moveToFirst() -> DeleteStatus.AlreadyAbsent
                it.isNull(0) || it.isNull(1) -> DeleteStatus.Unknown
                it.getLong(0) != item.size || it.getLong(1) != item.modifiedSeconds -> DeleteStatus.Changed
                else -> null
            }
        }
    } catch (_: Exception) {
        DeleteStatus.Unknown
    }
}

internal suspend fun authorizePendingMedia(
    resolver: ContentResolver,
    items: List<PendingMediaDelete>,
    authorize: suspend (IntentSender) -> Boolean,
    shouldStop: () -> Boolean = { false },
): Map<Uri, DeleteStatus> {
    val results = mutableMapOf<Uri, DeleteStatus>()
    val modern = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    for (batch in items.distinctBy { it.uri }.chunked(if (modern) 200 else 1)) {
        currentCoroutineContext().ensureActive()
        if (shouldStop()) break
        val ready =
            batch.filter { item ->
                val status = inspectMedia(resolver, item)
                if (status != null) results[item.uri] = status
                status == null
            }
        if (ready.isEmpty()) continue
        val sender = createMediaDeleteSender(resolver, ready)
        if (shouldStop()) break
        if (sender == null) {
            ready.forEach { results[it.uri] = DeleteStatus.Failed }
            continue
        }
        if (!authorize(sender)) {
            items.forEach { results.putIfAbsent(it.uri, DeleteStatus.AuthorizationCancelled) }
            break
        }
        for (item in ready) {
            results[item.uri] = finishAuthorizedMediaDelete(resolver, item, shouldStop)
        }
    }
    return results
}

internal fun finishAuthorizedMediaDelete(
    resolver: ContentResolver,
    item: PendingMediaDelete,
    shouldStop: () -> Boolean,
): DeleteStatus {
    val after = inspectMedia(resolver, item)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return when (after) {
            DeleteStatus.AlreadyAbsent -> DeleteStatus.Deleted
            null -> DeleteStatus.Failed
            else -> after
        }
    }
    if (after != null) return after
    if (shouldStop()) return DeleteStatus.Failed
    return try {
        if (resolver.delete(item.uri, null, null) > 0) DeleteStatus.Deleted else DeleteStatus.Unknown
    } catch (_: Exception) {
        DeleteStatus.Unknown
    }
}

internal fun createMediaDeleteSender(
    resolver: ContentResolver,
    items: List<PendingMediaDelete>,
): IntentSender? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        runCatching { MediaStore.createDeleteRequest(resolver, items.map { it.uri }).intentSender }.getOrNull()
    } else {
        items.single().recovery
    }
