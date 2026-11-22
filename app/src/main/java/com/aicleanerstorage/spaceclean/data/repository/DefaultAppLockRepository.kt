package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import androidx.core.content.edit
import com.aicleanerstorage.spaceclean.data.local.applock.LockableAppQuery
import com.aicleanerstorage.spaceclean.data.local.applock.PinCredential
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.rule.AppLockPinRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 鐢?SharedPreferences 涓?PackageManager 瀹炵幇搴旂敤閿侀厤缃€丳IN 涓庡彲閿佸簲鐢ㄦ煡璇€?*/
internal class DefaultAppLockRepository(
    context: Context,
    private val preferences: PreferencesDataSource,
) : AppLockRepository {
    private val appContext = context.applicationContext
    private val lockableAppQuery = LockableAppQuery(appContext)

    private val lockedAppCountFlow = MutableStateFlow(computeLockedAppCount())

    override fun observeLockedAppCount(): Flow<Int> = lockedAppCountFlow.asStateFlow()

    override fun currentLockedAppCount(): Int = lockedAppCountFlow.value

    override fun isPinSet(): Boolean =
        preferences.getBoolean(KEY_IS_SET_LOCK, false) &&
            (preferences.contains(KEY_PIN_HASH) || preferences.getString(KEY_PIN, "").length == AppLockPinRule.PIN_LENGTH)

    @Synchronized
    override fun savePin(pin: String) {
        require(pin.length == AppLockPinRule.PIN_LENGTH && pin.all(Char::isDigit))
        val encoded = PinCredential.create(pin)
        preferences.edit(commit = true) {
            putString(KEY_PIN_HASH, encoded)
            remove(KEY_PIN)
            putBoolean(KEY_IS_SET_LOCK, true)
        }
        lockedAppCountFlow.value = computeLockedAppCount()
    }

    @Synchronized
    override fun verifyPin(pin: String): Boolean {
        if (pin.length != AppLockPinRule.PIN_LENGTH) return false
        val hash = preferences.getStringOrNull(KEY_PIN_HASH)
        if (hash != null) return PinCredential.matches(pin, hash)
        if (preferences.getStringOrNull(KEY_PIN) != pin) return false
        savePin(pin)
        return true
    }

    override fun lockedPackages(): Set<String> = decodePackages(preferences.getString(KEY_LOCKED_PACKAGES, ""))

    private fun computeLockedAppCount(): Int = if (isPinSet() && isMonitoringEnabled()) lockedPackages().size else 0

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
        val encodedPackages =
            packageNames
                .asSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() && it != appContext.packageName }
                .distinct()
                .joinToString(",")
        preferences.edit(commit = true) { putString(KEY_LOCKED_PACKAGES, encodedPackages) }
        lockedAppCountFlow.value = computeLockedAppCount()
    }

    override suspend fun lockableApps(): List<AppLockAppModel> {
        val locked = if (isPinSet()) lockedPackages() else DEFAULT_LOCK_PACKAGES
        return lockableAppQuery
            .loadLaunchableApps()
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
        lockedAppCountFlow.value = computeLockedAppCount()
    }

    override fun isAutoLockEnabled(): Boolean = preferences.getBoolean(KEY_AUTO_LOCK, false)

    override fun setAutoLockEnabled(enabled: Boolean) {
        preferences.edit(commit = true) { putBoolean(KEY_AUTO_LOCK, enabled) }
    }

    override fun isVibrationEnabled(): Boolean = preferences.getBoolean(KEY_VIBRATE_ON_KEYPAD, true)

    override fun setVibrationEnabled(enabled: Boolean) {
        preferences.edit(commit = true) { putBoolean(KEY_VIBRATE_ON_KEYPAD, enabled) }
    }

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
        private const val KEY_PIN_HASH = "pin_hash_v1"
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
