package com.aicleanerstorage.spaceclean.feature.applock

import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringController
import com.aicleanerstorage.spaceclean.feature.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringService

internal class AppLockMonitoringControllerImpl(
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
