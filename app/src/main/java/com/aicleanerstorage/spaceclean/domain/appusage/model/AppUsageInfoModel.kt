package com.aicleanerstorage.spaceclean.domain.appusage.model

data class AppUsageInfoModel(
    val packageName: String,
    val appName: String,
    val totalForegroundMs: Long,
    val launchCount: Int,
)
