package com.aicleanerstorage.spaceclean.feature.applock

import com.aicleanerstorage.spaceclean.feature.applock.AppLockRepositoryImpl

object AppLockManager {
    fun isMonitoringEnabled(): Boolean = AppPrefsUtils.getBoolean(AppLockRepositoryImpl.KEY_MONITORING_ENABLED, true)

    fun isAppLocked(packageName: String): Boolean {
        val lockedStr = AppPrefsUtils.getString(AppLockRepositoryImpl.KEY_LOCKED_PACKAGES, "")
        if (lockedStr.isBlank()) return false
        return packageName in lockedStr.split(',').filter { it.isNotBlank() }.toSet()
    }
}
