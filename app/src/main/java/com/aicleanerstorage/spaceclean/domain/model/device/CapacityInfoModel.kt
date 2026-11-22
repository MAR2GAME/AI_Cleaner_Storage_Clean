package com.aicleanerstorage.spaceclean.domain.model.device

/** 内存或存储的容量信息。 */
data class CapacityInfoModel(
    val totalBytes: Long,
    val availableBytes: Long,
) {
    val usedBytes: Long get() = if (totalBytes > 0L) totalBytes - availableBytes else 0L

    val usagePercent: Int get() = if (totalBytes > 0L) (usedBytes.toFloat() / totalBytes * 100).toInt() else 0
}
