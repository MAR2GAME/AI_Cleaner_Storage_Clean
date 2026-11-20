package com.aicleanerstorage.spaceclean.domain.model

data class CleanupSummaryModel(
    val freedSpace: Long,
    val cleanedCount: Int,
    val failedCount: Int,
    val memoryFreedBytes: Long,
    val memoryProcessesKilled: Int,
) {
    val totalFreedBytes: Long
        get() = freedSpace + memoryFreedBytes

    val hasVisibleResult: Boolean
        get() = totalFreedBytes > 0L || cleanedCount > 0 || failedCount > 0 || memoryProcessesKilled > 0
}
