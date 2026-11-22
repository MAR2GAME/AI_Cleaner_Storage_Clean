package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.port.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusThreatModel
import java.io.File

/** 用 PackageManager 与偏好存储实现病毒扫描记录和拦截状态。 */
internal class DefaultVirusSecurityRepository(
    context: Context,
    private val preferences: PreferencesDataSource,
) : VirusSecurityRepository {
    private val appContext = context.applicationContext

    override fun isScanNoticeAccepted(): Boolean = preferences.getBoolean(KEY_VIRUS_SCAN_NOTICE_ACCEPTED, false)

    override fun setScanNoticeAccepted() {
        preferences.edit(commit = true) { putBoolean(KEY_VIRUS_SCAN_NOTICE_ACCEPTED, true) }
    }

    override fun hasInstalledAppsAccessFailedBefore(): Boolean = preferences.getBoolean(KEY_VIRUS_INSTALLED_APPS_ACCESS_FAILED_ONCE, false)

    override fun setInstalledAppsAccessFailed(failed: Boolean) {
        if (failed) {
            preferences.edit { putBoolean(KEY_VIRUS_INSTALLED_APPS_ACCESS_FAILED_ONCE, true) }
        } else {
            preferences.edit { remove(KEY_VIRUS_INSTALLED_APPS_ACCESS_FAILED_ONCE) }
        }
    }

    override fun appLabel(packageName: String): String = resolveAppLabel(packageName)

    private fun resolveAppLabel(packageName: String): String {
        if (packageName.isBlank()) return ""
        return try {
            val packageManager = appContext.packageManager
            val appInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                }
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName
        }
    }

    override fun threatFor(
        item: VirusScanItemModel,
        isFile: Boolean,
    ): VirusThreatModel = item.toThreat(isFile)

    override fun hasInstalledAppsAccess(): Boolean {
        val packageManager = appContext.packageManager
        val currentPackageName = appContext.packageName
        val installedPackages =
            try {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_API_30_PLUS
                    } else {
                        TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_LEGACY
                    },
                )
            } catch (_: Exception) {
                return false
            }

        return installedPackages.any { packageInfo ->
            packageInfo.packageName.isNotBlank() && packageInfo.packageName != currentPackageName
        }
    }

    override fun hasAdbRisk(): Boolean {
        val adbEnabled =
            try {
                Settings.Global.getInt(appContext.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
            } catch (_: Exception) {
                runCatching {
                    Settings.Secure.getInt(appContext.contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1
                }.getOrDefault(false)
            }
        if (!adbEnabled) return false

        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        return intent.resolveActivityInfo(appContext.packageManager, 0) != null
    }

    private companion object {
        private const val TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_API_30_PLUS = 131072
        private const val TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_LEGACY = 64
    }
}

private fun VirusScanItemModel.toThreat(isFile: Boolean): VirusThreatModel {
    val path = apkPath?.takeIf { it.isNotBlank() }
    val packageName = packageName?.takeIf { it.isNotBlank() }
    val description = threatDescription()
    return if (isFile || appName.isNullOrBlank()) {
        VirusThreatModel(
            id = "file:${path ?: description}",
            packageName = packageName,
            apkPath = path,
            title = path?.let { File(it).name } ?: threatName.orEmpty(),
            description = description,
            isFile = true,
            icon = null,
        )
    } else {
        VirusThreatModel(
            id = "app:${packageName ?: appName}",
            packageName = packageName,
            apkPath = path,
            title = appName,
            description = description,
            isFile = false,
            icon = packageName,
        )
    }
}

private fun VirusScanItemModel.threatDescription(): String =
    description?.takeIf { it.isNotBlank() }
        ?: threatName?.takeIf { it.isNotBlank() }
        ?: ""

private const val KEY_VIRUS_SCAN_NOTICE_ACCEPTED = "virus_scan_notice_accepted"
private const val KEY_VIRUS_INSTALLED_APPS_ACCESS_FAILED_ONCE = "virus_installed_apps_access_failed_once"
