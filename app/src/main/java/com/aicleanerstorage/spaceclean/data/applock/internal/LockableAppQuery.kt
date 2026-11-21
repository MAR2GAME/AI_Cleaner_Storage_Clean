package com.aicleanerstorage.spaceclean.data.applock.internal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.aicleanerstorage.spaceclean.data.applock.model.AppLockAppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LockableAppQuery(
    private val context: Context,
) {
    suspend fun loadLaunchableApps(): List<AppLockAppModel> =
        withContext(Dispatchers.IO) {
            val packageManager = context.packageManager
            val currentAppPackage = context.packageName
            getLauncherApps(packageManager, currentAppPackage)
                .distinctBy { it.packageName }
                .sortedBy { it.appName.lowercase() }
        }

    @Suppress("DEPRECATION")
    private fun getLauncherApps(
        packageManager: PackageManager,
        currentAppPackage: String,
    ): List<AppLockAppModel> {
        val mainIntent =
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
        val resolveInfos =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    mainIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
                )
            } else {
                packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
            }
        return resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
            createLockAppInfoSafely(packageManager, packageName, currentAppPackage)
        }
    }

    private fun createLockAppInfoSafely(
        packageManager: PackageManager,
        packageName: String,
        currentAppPackage: String,
    ): AppLockAppModel? {
        if (packageName == currentAppPackage) return null
        return runCatching {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            AppLockAppModel(
                packageName = packageName,
                appName = applicationInfo.loadLabel(packageManager).toString(),
                isLocked = false,
            )
        }.getOrNull()
    }
}
