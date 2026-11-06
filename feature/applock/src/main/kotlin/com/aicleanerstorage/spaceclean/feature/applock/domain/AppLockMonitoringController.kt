package com.aicleanerstorage.spaceclean.feature.applock.domain

import com.aicleanerstorage.spaceclean.feature.applock.domain.AppLockMonitoringController

interface AppLockMonitoringController {
    fun enableMonitoring()

    fun disableMonitoring()

    fun syncMonitoringService()
}
