package com.aicleanerstorage.spaceclean.data.security.internal

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.aicleanerstorage.spaceclean.data.security.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanItemModel
import com.aicleanerstorage.spaceclean.data.security.model.VirusThreatModel
import java.io.File

internal class VirusSecurityRepositoryImpl(
    context: Context,
) : VirusSecurityRepository {
    private val appContext = context.applicationContext

    private val preferences = appContext.getSharedPreferences("quick_clean_settings", Context.MODE_PRIVATE)

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

    override fun appLabel(packageName: String): String = installedAppLabel(appContext, packageName)

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
            title = path?.let { File(it).name } ?: virusName.orEmpty(),
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

private fun VirusScanItemModel.threatDescription(): String {
    val summaryText =
        runCatching {
            if (summary != null && summary.size > 1) summary[1] else null
        }.getOrNull()
    return summaryText?.takeIf { it.isNotBlank() }
        ?: virusName?.takeIf { it.isNotBlank() }
        ?: ""
}

private const val KEY_VIRUS_SCAN_NOTICE_ACCEPTED = "virus_scan_notice_accepted"
private const val KEY_VIRUS_INSTALLED_APPS_ACCESS_FAILED_ONCE = "virus_installed_apps_access_failed_once"
