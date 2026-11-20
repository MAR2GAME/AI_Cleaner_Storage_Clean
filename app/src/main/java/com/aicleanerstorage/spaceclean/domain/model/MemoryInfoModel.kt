package com.aicleanerstorage.spaceclean.domain.model

data class MemoryInfoModel(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val usagePercent: Int,
    val isTotalValid: Boolean,
)
