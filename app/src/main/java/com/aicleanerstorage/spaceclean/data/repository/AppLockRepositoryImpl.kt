package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.data.local.AdvancedLockAppInfoHelper
import com.aicleanerstorage.spaceclean.data.local.AppLockPermissionUtils
import com.aicleanerstorage.spaceclean.domain.model.AppLockAppModel
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.rule.AppLockPinRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AppLockRepositoryImpl(
    context: Context,
) : AppLockRepository {
    private val appContext = context.applicationContext
    private val preferences = PreferencesUtil(appContext, PREFS_NAME)
    private val appInfoHelper = AdvancedLockAppInfoHelper(appContext)

    private val _lockedAppCountFlow = MutableStateFlow(lockedAppCount())
    override val lockedAppCountFlow: StateFlow<Int> = _lockedAppCountFlow.asStateFlow()

    override fun isPinSet(): Boolean =
        preferences.getBoolean(KEY_IS_SET_LOCK, false) &&
            preferences.getString(KEY_PIN, "").length == AppLockPinRule.PIN_LENGTH

    override fun savePin(pin: String) {
        preferences.edit(commit = true) {
            putString(KEY_PIN, pin.take(AppLockPinRule.PIN_LENGTH))
            putBoolean(KEY_IS_SET_LOCK, true)
        }
        _lockedAppCountFlow.value = lockedAppCount()
    }

    override fun verifyPin(pin: String): Boolean = pin.length == AppLockPinRule.PIN_LENGTH && pin == preferences.getString(KEY_PIN, "")

    override fun lockedPackages(): Set<String> = decodePackages(preferences.getString(KEY_LOCKED_PACKAGES, ""))

    private fun lockedAppCount(): Int =
        if (isPinSet() && isMonitoringEnabled()) lockedPackages().size else 0

    override fun isPackageLocked(packageName: String): Boolean = packageName in lockedPackages()

    override fun setPackageLocked(
        packageName: String,
        locked: Boolean,
    ) {
        if (packageName.isBlank() || packageName == appContext.packageName) return
        val current = lockedPackages().toMutableSet()
        if (locked) current += packageName else current -= packageName
        setLockedPackages(current)
    }

    override fun setLockedPackages(packageNames: Set<String>) {
        val encodedPackages = packageNames
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() && it != appContext.packageName }
            .distinct()
            .joinToString(",")
        preferences.edit(commit = true) { putString(KEY_LOCKED_PACKAGES, encodedPackages) }
        _lockedAppCountFlow.value = lockedAppCount()
    }

    override suspend fun lockableApps(): List<AppLockAppModel> {
        val locked = if (isPinSet()) lockedPackages() else DEFAULT_LOCK_PACKAGES
        return appInfoHelper
            .getApps()
            .map { app ->
                app.copy(isLocked = app.packageName in locked)
            }.sortedWith(
                compareBy<AppLockAppModel> { it.appName.lowercase() }
                    .thenBy { it.packageName },
            )
    }

    override fun isMonitoringEnabled(): Boolean = preferences.getBoolean(KEY_MONITORING_ENABLED, true)

    override fun setMonitoringEnabled(enabled: Boolean) {
        preferences.edit(commit = true) { putBoolean(KEY_MONITORING_ENABLED, enabled) }
        _lockedAppCountFlow.value = lockedAppCount()
    }

    override fun isAutoLockEnabled(): Boolean = preferences.getBoolean(KEY_AUTO_LOCK, false)

    override fun setAutoLockEnabled(enabled: Boolean) {
        preferences.edit(commit = true) { putBoolean(KEY_AUTO_LOCK, enabled) }
    }

    override fun isVibrationEnabled(): Boolean = preferences.getBoolean(KEY_VIBRATE_ON_KEYPAD, true)

    override fun setVibrationEnabled(enabled: Boolean) {
        preferences.edit(commit = true) { putBoolean(KEY_VIBRATE_ON_KEYPAD, enabled) }
    }

    override fun hasUsageAccess(): Boolean = AppLockPermissionUtils.hasUsageStatsPermission(appContext)

    override fun hasOverlayPermission(): Boolean = AppLockPermissionUtils.canDrawOverlays(appContext)

    override fun handlePackageAdded(packageName: String) {
        if (isPinSet() && isAutoLockEnabled() && isLaunchable(packageName)) {
            setPackageLocked(packageName, true)
        }
    }

    private fun isLaunchable(packageName: String): Boolean =
        packageName.isNotBlank() &&
            packageName != appContext.packageName &&
            appContext.packageManager.getLaunchIntentForPackage(packageName) != null

    private fun decodePackages(value: String): Set<String> =
        value
            .split(',')
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()

    companion object {
        const val PREFS_NAME = "phone_cleaner_sp"
        const val KEY_PIN = "pin"
        const val KEY_IS_SET_LOCK = "is_set_lock"
        const val KEY_LOCKED_PACKAGES = "lockApp_packageNames"
        const val KEY_MONITORING_ENABLED = "app_lock_monitoring_enabled"
        const val KEY_AUTO_LOCK = "turn_on_auto_lock"
        const val KEY_VIBRATE_ON_KEYPAD = "vibrate_on_keypad"

        private val DEFAULT_LOCK_PACKAGES =
            setOf(
                "com.google.android.gm",
                "com.google.android.apps.photos",
                "com.facebook.katana",
                "com.instagram.android",
                "com.twitter.android",
                "com.whatsapp",
                "com.sec.android.gallery3d",
            )
    }
}
