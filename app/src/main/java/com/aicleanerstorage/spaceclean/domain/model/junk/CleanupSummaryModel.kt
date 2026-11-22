package com.aicleanerstorage.spaceclean.domain.model.junk

/** 一次清理的汇总；用于结果页展示是否产生了可见变化。 */
data class CleanupSummaryModel(
    val freedSpace: Long = 0L,
    val cleanedCount: Int = 0,
    val failedCount: Int = 0,
    val memoryFreedBytes: Long = 0L,
) {
    /** 是否有需要展示的清理结果。 */
    val hasVisibleResult: Boolean
        get() = freedSpace > 0L || cleanedCount > 0 || failedCount > 0
}
