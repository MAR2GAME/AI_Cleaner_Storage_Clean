package com.aicleanerstorage.spaceclean.feature.toolbox.data.appusage

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository


import android.content.Context
import com.aicleanerstorage.spaceclean.feature.toolbox.data.appusage.AppUsageDataSource
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageInfo

class AppUsageRepositoryImpl(
    context: Context,
) : AppUsageRepository {
    private val appContext = context.applicationContext

    override fun hasAppUsageAccess(): Boolean = AppUsageDataSource.hasUsageAccess(appContext)

    override fun resetAppUsagePermissionCache() {
        AppUsageDataSource.resetPermissionCache()
    }

    override suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfo> = AppUsageDataSource.usageBetween(appContext, startMillis, endMillis)

    override suspend fun runningPackages(packageNames: Set<String>): Set<String> =
        AppUsageDataSource.runningPackages(appContext, packageNames)
}

