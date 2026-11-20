package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.AppLockAppModel
import kotlinx.coroutines.flow.StateFlow

interface AppLockRepository {
    val lockedAppCountFlow: StateFlow<Int>
    fun isPinSet(): Boolean

    fun savePin(pin: String)

    fun verifyPin(pin: String): Boolean

    fun lockedPackages(): Set<String>

    fun isPackageLocked(packageName: String): Boolean

    fun setPackageLocked(
        packageName: String,
        locked: Boolean,
    )

    fun setLockedPackages(packageNames: Set<String>)

    suspend fun lockableApps(): List<AppLockAppModel>

    fun isMonitoringEnabled(): Boolean

    fun setMonitoringEnabled(enabled: Boolean)

    fun isAutoLockEnabled(): Boolean

    fun setAutoLockEnabled(enabled: Boolean)

    fun isVibrationEnabled(): Boolean

    fun setVibrationEnabled(enabled: Boolean)

    fun hasUsageAccess(): Boolean

    fun hasOverlayPermission(): Boolean

    fun handlePackageAdded(packageName: String)
}
