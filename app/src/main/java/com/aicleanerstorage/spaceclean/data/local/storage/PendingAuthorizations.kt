package com.aicleanerstorage.spaceclean.data.local.storage

import java.util.UUID

/** The Android adapter serializes access, including validation before issuing a request. */
internal class PendingAuthorizations<K, V> {
    private data class Entry<K, V>(
        val key: K,
        val value: V,
        var requestId: String? = null,
    )

    private val entries = mutableMapOf<String, Entry<K, V>>()

    fun invalidate(matches: (K) -> Boolean) {
        entries.values.removeAll { matches(it.key) }
    }

    fun register(
        key: K,
        value: V,
    ): String {
        invalidate { it == key }
        val id = UUID.randomUUID().toString()
        entries[id] = Entry(key, value)
        return id
    }

    fun discard(ids: Collection<String>) {
        ids.forEach(entries::remove)
    }

    fun pending(ids: List<String>): List<V>? =
        ids.map { id ->
            val entry = entries[id] ?: return null
            if (entry.requestId != null) return null
            entry.value
        }

    fun markRequested(
        ids: List<String>,
        requestId: String,
    ) {
        ids.forEach { entries.getValue(it).requestId = requestId }
    }

    fun consume(requestId: String): Map<String, V> {
        val batch = entries.filterValues { it.requestId == requestId }
        discard(batch.keys)
        return batch.mapValues { it.value.value }
    }
}
