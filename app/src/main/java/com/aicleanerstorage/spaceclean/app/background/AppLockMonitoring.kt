package com.aicleanerstorage.spaceclean.app.background

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.rule.AppLockMonitoringRule

internal fun syncAppLockMonitoring(context: Context, repository: AppLockRepository) {
    if (AppLockMonitoringRule.shouldMonitorApps(repository)) {
        AppBackgroundRuntimeService.enableMonitoring(context)
    } else {
        AppBackgroundRuntimeService.disableMonitoring(context)
    }
}
