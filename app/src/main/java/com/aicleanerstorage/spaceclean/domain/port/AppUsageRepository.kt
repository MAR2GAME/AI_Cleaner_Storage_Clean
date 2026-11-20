package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.AppUsageInfoModel

interface AppUsageRepository {
    fun hasAppUsageAccess(refresh: Boolean = false): Boolean

    suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfoModel>

    suspend fun runningPackages(packageNames: Set<String>): Set<String>
}

