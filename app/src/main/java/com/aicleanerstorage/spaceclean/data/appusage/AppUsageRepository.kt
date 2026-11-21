package com.aicleanerstorage.spaceclean.data.appusage

import com.aicleanerstorage.spaceclean.data.appusage.model.AppUsageInfoModel

interface AppUsageRepository {
    fun hasAppUsageAccess(refresh: Boolean = false): Boolean

    suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfoModel>

    suspend fun runningPackages(packageNames: Set<String>): Set<String>

    /**
     * Package currently in the foreground, or null when usage access is missing or
     * the system reports nothing. Used by the App Lock monitor.
     */
    suspend fun foregroundPackage(): String?
}
