package com.aicleanerstorage.spaceclean.feature.applock.data

import com.aicleanerstorage.spaceclean.feature.applock.domain.AppLockMonitoringController

import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockRepository

import com.aicleanerstorage.spaceclean.feature.applock.data.AppLockMonitoringService

class AppLockMonitoringControllerImpl(
    private val repository: AppLockRepository,
    private val monitoringService: AppLockMonitoringService,
) : AppLockMonitoringController {
    override fun enableMonitoring() {
        repository.setMonitoringEnabled(true)
        monitoringService.enable()
    }

    override fun disableMonitoring() {
        repository.setMonitoringEnabled(false)
        monitoringService.disable()
    }

    override fun syncMonitoringService() {
        if (repository.isPinSet() &&
            repository.isMonitoringEnabled() &&
            repository.lockedAppCount() > 0 &&
            repository.hasOverlayPermission() &&
            repository.hasUsageAccess()
        ) {
            monitoringService.enable()
        } else {
            monitoringService.disable()
        }
    }
}
