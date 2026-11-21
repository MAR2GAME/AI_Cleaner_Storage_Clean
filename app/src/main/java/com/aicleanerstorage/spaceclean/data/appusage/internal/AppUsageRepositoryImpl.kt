package com.aicleanerstorage.spaceclean.data.appusage.internal

import android.content.Context
import com.aicleanerstorage.spaceclean.data.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.data.appusage.model.AppUsageInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AppUsageRepositoryImpl(
    context: Context,
) : AppUsageRepository {
    private val appContext = context.applicationContext

    override fun hasAppUsageAccess(refresh: Boolean): Boolean = AppUsageDataSource.hasUsageAccess(appContext, refresh)

    override suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfoModel> =
        withContext(Dispatchers.IO) {
            if (!hasAppUsageAccess() || endMillis <= startMillis) return@withContext emptyList()
            AppUsageDataSource
                .readUsage(appContext, startMillis, endMillis)
                .map { usage ->
                    AppUsageInfoModel(
                        packageName = usage.packageName,
                        appName = AppUsageDataSource.appLabel(appContext, usage.packageName),
                        totalForegroundMs = usage.foregroundMillis,
                        launchCount = usage.launches,
                    )
                }.filter { it.totalForegroundMs > 0L || it.launchCount > 0 }
                .sortedWith(
                    compareByDescending<AppUsageInfoModel> { it.totalForegroundMs }
                        .thenByDescending { it.launchCount }
                        .thenBy { it.appName.lowercase() },
                )
        }

    override suspend fun runningPackages(packageNames: Set<String>): Set<String> =
        AppUsageDataSource.runningPackages(appContext, packageNames)

    override suspend fun foregroundPackage(): String? = withContext(Dispatchers.IO) { AppUsageDataSource.foregroundPackage(appContext) }
}
