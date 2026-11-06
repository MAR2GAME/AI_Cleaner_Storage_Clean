package com.aicleanerstorage.spaceclean.core.contracts.feature

interface CleanupHistoryStore {
    fun hasCompletedCleanup(): Boolean

    fun recordCompletedCleanup()
}

