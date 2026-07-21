package com.aicleanerstorage.spaceclean.feature.toolbox.appusage.data

import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageInfo

interface AppUsageRepository {
    fun hasAppUsageAccess(): Boolean

    fun resetAppUsagePermissionCache()

    suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfo>

    suspend fun runningPackages(packageNames: Set<String>): Set<String>
}
