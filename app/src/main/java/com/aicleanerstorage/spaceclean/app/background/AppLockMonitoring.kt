package com.aicleanerstorage.spaceclean.app.background

import android.content.Context
import com.aicleanerstorage.spaceclean.data.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.data.applock.AppLockMonitoringRule

internal fun syncAppLockMonitoring(context: Context, repository: AppLockRepository) {
    if (AppLockMonitoringRule.shouldMonitorApps(repository)) {
        AppBackgroundRuntimeService.enableMonitoring(context)
    } else {
        AppBackgroundRuntimeService.disableMonitoring(context)
    }
}
