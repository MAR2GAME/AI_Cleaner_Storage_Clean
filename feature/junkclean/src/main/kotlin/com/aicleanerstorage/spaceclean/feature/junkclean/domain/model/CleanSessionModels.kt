package com.aicleanerstorage.spaceclean.feature.junkclean.domain.model

data class ScanSessionState(
    val progress: ScanProgress = ScanProgress.IDLE,
    val scanResult: ScanResult? = null,
)

data class CleanupSummary(
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
