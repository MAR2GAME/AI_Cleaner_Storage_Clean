package com.aicleanerstorage.spaceclean.app.background

import android.content.Context
import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringService

internal class BackgroundAppLockMonitoringService(context: Context) : AppLockMonitoringService {
    private val appContext = context.applicationContext

    override fun enable() = AppBackgroundRuntimeService.enableMonitoring(appContext)

    override fun disable() = AppBackgroundRuntimeService.disableMonitoring(appContext)
}
