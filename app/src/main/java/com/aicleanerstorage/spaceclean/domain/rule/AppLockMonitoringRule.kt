package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository

object AppLockMonitoringRule {
    fun shouldMonitorApps(repository: AppLockRepository): Boolean =
        repository.isPinSet() &&
            repository.isMonitoringEnabled() &&
            repository.lockedPackages().isNotEmpty() &&
            repository.hasOverlayPermission() &&
            repository.hasUsageAccess()
}
