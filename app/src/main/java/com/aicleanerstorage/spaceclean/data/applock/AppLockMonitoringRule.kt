package com.aicleanerstorage.spaceclean.data.applock

object AppLockMonitoringRule {
    fun shouldMonitorApps(repository: AppLockRepository): Boolean =
        repository.isPinSet() &&
            repository.isMonitoringEnabled() &&
            repository.lockedPackages().isNotEmpty() &&
            repository.hasOverlayPermission() &&
            repository.hasUsageAccess()
}
