package com.aicleanerstorage.spaceclean.common.storage.preferences

interface CleanupHistoryStore {
    fun hasCompletedCleanup(): Boolean

    fun recordCompletedCleanup()
}

class DefaultCleanupHistoryStore internal constructor(
    private val readCount: () -> Long,
    private val writeCount: (Long) -> Unit,
) : CleanupHistoryStore {
    constructor(appPreferences: AppPreferences) : this(
        readCount = { appPreferences.store.getLong(KEY_COMPLETED_CLEANUP_COUNT, 0L) },
        writeCount = { count ->
            appPreferences.store.edit().putLong(KEY_COMPLETED_CLEANUP_COUNT, count).apply()
        },
    )

    override fun hasCompletedCleanup(): Boolean = readCount() > 0L

    override fun recordCompletedCleanup() {
        writeCount(readCount() + 1L)
    }
}

private const val KEY_COMPLETED_CLEANUP_COUNT = "cleanup_completed_count"
