package com.aicleanerstorage.spaceclean.data.appusage

import com.aicleanerstorage.spaceclean.data.appusage.model.AppUsageInfoModel

interface AppUsageRepository {
    fun hasAppUsageAccess(refresh: Boolean = false): Boolean

    suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfoModel>

    suspend fun runningPackages(packageNames: Set<String>): Set<String>
}

