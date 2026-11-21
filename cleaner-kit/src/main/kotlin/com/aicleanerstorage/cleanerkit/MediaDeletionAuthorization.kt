package com.aicleanerstorage.cleanerkit

import com.aicleanerstorage.cleanerkit.internal.storage.PendingAuthorizations

import android.content.ContentResolver
import android.content.IntentSender
import android.os.Build
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.internal.storage.PendingMediaDelete
import com.aicleanerstorage.cleanerkit.internal.storage.createMediaDeleteSender
import com.aicleanerstorage.cleanerkit.internal.storage.finishAuthorizedMediaDelete
import com.aicleanerstorage.cleanerkit.internal.storage.inspectMedia
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaDeletionRequest internal constructor(val requestId: String, val sender: IntentSender)

/** Android adapter for hosts which launch media confirmation themselves. */
class MediaDeletionAuthorization internal constructor(private val resolver: ContentResolver) {
    internal enum class Owner { Clean, Files }

    private data class Entry(
        val media: PendingMediaDelete,
        val current: () -> Boolean,
        val finished: (DeleteStatus) -> Unit,
    )

    private val entries = PendingAuthorizations<Pair<Owner, String>, Entry>()
    val maxBatchSize: Int get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) 200 else 1

    @Synchronized
    internal fun invalidate(owner: Owner, key: String? = null) {
        entries.invalidate { (entryOwner, entryKey) -> entryOwner == owner && (key == null || entryKey == key) }
    }

    @Synchronized
    internal fun register(
        owner: Owner,
        key: String,
        media: PendingMediaDelete,
        current: () -> Boolean = { true },
        finished: (DeleteStatus) -> Unit = {},
    ): String {
        return entries.register(owner to key, Entry(media, current, finished))
    }

    /** Release abandoned IDs, including any outstanding confirmation for those entries. */
    @Synchronized
    fun discard(ids: Collection<String>) {
        entries.discard(ids)
    }

    suspend fun prepare(ids: List<String>): MediaDeletionRequest? = withContext(Dispatchers.IO) {
        synchronized(this@MediaDeletionAuthorization) {
            val unique = ids.distinct()
            require(unique.size in 1..maxBatchSize)
            val batch = entries.pending(unique) ?: return@synchronized null
            if (batch.any { !it.current() || inspectMedia(resolver, it.media) != null }) {
                return@synchronized null
            }
            val sender = createMediaDeleteSender(resolver, batch.map { it.media }) ?: return@synchronized null
            val requestId = UUID.randomUUID().toString()
            entries.markRequested(unique, requestId)
            MediaDeletionRequest(requestId, sender)
        }
    }

    /** The returned map contains only item IDs consumed by this callback. */
    suspend fun complete(requestId: String, approved: Boolean): Map<String, DeleteStatus> = withContext(Dispatchers.IO) {
        synchronized(this@MediaDeletionAuthorization) {
            entries.consume(requestId).mapValues { (_, entry) ->
                val status = if (approved && entry.current()) {
                    finishAuthorizedMediaDelete(resolver, entry.media) { !entry.current() }
                } else DeleteStatus.Failed
                entry.finished(status)
                status
            }
        }
    }
}
