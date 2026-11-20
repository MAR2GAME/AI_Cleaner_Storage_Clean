package com.aicleanerstorage.spaceclean.domain.model

data class StorageInfoModel(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
) {

    val usagePercent: Int get() = if (totalBytes > 0) ((usedBytes.toFloat() / totalBytes) * 100).toInt() else 0

}
