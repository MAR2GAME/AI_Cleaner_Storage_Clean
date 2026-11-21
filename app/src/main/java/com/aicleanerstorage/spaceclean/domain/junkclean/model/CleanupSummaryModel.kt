package com.aicleanerstorage.spaceclean.domain.junkclean.model

data class CleanupSummaryModel(
    val freedSpace: Long = 0L,
    val cleanedCount: Int = 0,
    val failedCount: Int = 0,
    val memoryFreedBytes: Long = 0L,
) {
    val hasVisibleResult: Boolean
        get() = freedSpace > 0L || cleanedCount > 0 || failedCount > 0
}
