package com.aicleanerstorage.spaceclean.feature.antivirus.data

import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanItem
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusThreat
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.TrustlookApiKeyState


import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings


class VirusSecurityRepositoryImpl(
    context: Context,
    private val apiKey: String,
) : VirusSecurityRepository {
    private val appContext = context.applicationContext

    override fun reportScanError(code: Int, message: String?) {
        logScanError(code, message, trustlookApiKeyState().logState)
    }

    override fun appLabel(packageName: String): String = getAppLabelAndIcon(appContext, packageName).first
    override fun threatFor(item: VirusScanItem, isFile: Boolean): VirusThreat = item.toThreat(appContext, isFile)
    override fun reportStartError(error: Throwable) {
        android.util.Log.w("VirusScan", "Trustlook scan start failed apiKey=${trustlookApiKeyState().logState}", error)
    }

    override fun hasInstalledAppsAccess(): Boolean {
        val packageManager = appContext.packageManager
        val currentPackageName = appContext.packageName
        val installedPackages = try {
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
        val adbEnabled = try {
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

    override fun trustlookApiKeyState(): TrustlookApiKeyState {
        val apiKey = this.apiKey
        return TrustlookApiKeyState(
            isConfigured = apiKey.isNotBlank(),
            logState = if (apiKey.isBlank()) {
                "blank"
            } else {
                "present(length=${apiKey.length}, suffix=${apiKey.takeLast(6)})"
            },
        )
    }

    private companion object {
        private const val TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_API_30_PLUS = 131072
        private const val TRUSTLOOK_INSTALLED_PACKAGES_FLAGS_LEGACY = 64
    }
}
