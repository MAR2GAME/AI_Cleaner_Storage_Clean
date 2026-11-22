package com.aicleanerstorage.spaceclean.domain.model.appusage

data class AppUsageInfoModel(
    val packageName: String,
    val appName: String,
    val totalForegroundMs: Long,
    val launchCount: Int,
)
