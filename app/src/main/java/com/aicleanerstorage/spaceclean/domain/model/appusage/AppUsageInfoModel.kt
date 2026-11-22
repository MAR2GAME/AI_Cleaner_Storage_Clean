package com.aicleanerstorage.spaceclean.domain.model.appusage

/** 单个应用在查询区间内的使用统计。 */
data class AppUsageInfoModel(
    val packageName: String,
    val appName: String,
    val totalForegroundMs: Long,
    val launchCount: Int,
)
