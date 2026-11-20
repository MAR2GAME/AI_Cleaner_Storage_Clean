package com.aicleanerstorage.spaceclean.data.local

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

internal fun installedAppLabel(context: Context, packageName: String): String {
    if (packageName.isBlank()) return ""
    return try {
        val packageManager = context.packageManager
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        }
        packageManager.getApplicationLabel(appInfo).toString()
    } catch (error: Exception) {
        packageName
    }
}
