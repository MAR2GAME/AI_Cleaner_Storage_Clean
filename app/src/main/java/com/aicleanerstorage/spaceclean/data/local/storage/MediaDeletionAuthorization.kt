package com.aicleanerstorage.spaceclean.data.local.storage

import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete

/**
 * 保存等待系统授权的删除条目；只服务于 data 层的文件/垃圾删除实现。
 */
internal class MediaDeletionAuthorization {
    internal enum class Owner { Clean, Files }

    private data class Entry(
        val media: PendingMediaDelete,
        val current: () -> Boolean,
        val finished: (DeleteStatus) -> Unit,
    )

    private val entries = PendingAuthorizations<Pair<Owner, String>, Entry>()

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
    ): String = entries.register(owner to key, Entry(media, current, finished))
}
