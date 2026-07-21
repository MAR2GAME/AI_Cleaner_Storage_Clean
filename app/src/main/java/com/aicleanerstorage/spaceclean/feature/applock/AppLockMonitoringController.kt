package com.aicleanerstorage.spaceclean.feature.applock

interface AppLockMonitoringController {
    fun enableMonitoring()

    fun disableMonitoring()

    fun syncMonitoringService()
}
