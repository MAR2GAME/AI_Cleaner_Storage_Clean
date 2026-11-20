package com.aicleanerstorage.devicekit.model

data class CapacityInfo(
    val totalBytes: Long,
    val availableBytes: Long,
) {
    val usedBytes: Long get() = if (totalBytes > 0L) totalBytes - availableBytes else 0L
    val usagePercent: Int get() = if (totalBytes > 0L) (usedBytes.toFloat() / totalBytes * 100).toInt() else 0
}

typealias MemoryInfo = CapacityInfo
typealias StorageInfo = CapacityInfo
