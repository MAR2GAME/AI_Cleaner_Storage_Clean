package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.feature.applock.service.AppLockBroadcastActions
import com.aicleanerstorage.spaceclean.feature.applock.service.AppLockServiceActions
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatterySamplingServiceActions

internal object PersistentServiceActions {
    const val START = "com.aicleanerstorage.spaceclean.notification.START"
    const val ENABLE_MONITORING = "com.aicleanerstorage.spaceclean.applock.ENABLE_MONITORING"
    const val DISABLE_MONITORING = "com.aicleanerstorage.spaceclean.applock.DISABLE_MONITORING"
    const val ENABLE_BATTERY_SAMPLING = "com.aicleanerstorage.spaceclean.battery.ENABLE_SAMPLING"
    const val DISABLE_BATTERY_SAMPLING = "com.aicleanerstorage.spaceclean.battery.DISABLE_SAMPLING"
    const val RESTORE_NOTIFICATION = "com.aicleanerstorage.spaceclean.notification.RESTORE_PERSISTENT"
    const val STOP_SERVICE = "com.aicleanerstorage.spaceclean.notification.STOP_SERVICE"
    const val PASSWORD_SUCCESS = AppLockBroadcastActions.PASSWORD_SUCCESS
    const val LOCK_SCREEN_CANCELLED = AppLockBroadcastActions.LOCK_SCREEN_CANCELLED
}

internal enum class PersistentServiceCommand {
    Start,
    EnableMonitoring,
    DisableMonitoring,
    EnableBatterySampling,
    DisableBatterySampling,
    RestoreNotification,
    StopService,
    DismissLockScreen,
}

internal fun persistentServiceCommand(action: String?): PersistentServiceCommand? =
    when (action) {
        PersistentServiceActions.START -> PersistentServiceCommand.Start
        PersistentServiceActions.ENABLE_MONITORING -> PersistentServiceCommand.EnableMonitoring
        PersistentServiceActions.DISABLE_MONITORING -> PersistentServiceCommand.DisableMonitoring
        PersistentServiceActions.ENABLE_BATTERY_SAMPLING -> PersistentServiceCommand.EnableBatterySampling
        PersistentServiceActions.DISABLE_BATTERY_SAMPLING -> PersistentServiceCommand.DisableBatterySampling
        PersistentServiceActions.RESTORE_NOTIFICATION -> PersistentServiceCommand.RestoreNotification
        PersistentServiceActions.STOP_SERVICE -> PersistentServiceCommand.StopService
        PersistentServiceActions.PASSWORD_SUCCESS,
        PersistentServiceActions.LOCK_SCREEN_CANCELLED,
        -> PersistentServiceCommand.DismissLockScreen
        else -> null
    }

internal interface PersistentNotificationActions {
    fun scheduleRestore()
}

internal class PersistentServiceCommandDispatcher(
    private val appLock: AppLockServiceActions,
    private val batterySampling: BatterySamplingServiceActions,
    private val notification: PersistentNotificationActions,
    private val stopService: () -> Unit,
) {
    fun dispatch(command: PersistentServiceCommand) {
        when (command) {
            PersistentServiceCommand.Start -> {
                appLock.syncMonitoringState()
                batterySampling.syncSamplingState()
            }
            PersistentServiceCommand.EnableMonitoring -> appLock.enableMonitoring()
            PersistentServiceCommand.DisableMonitoring -> appLock.disableMonitoring()
            PersistentServiceCommand.EnableBatterySampling -> batterySampling.enableSampling()
            PersistentServiceCommand.DisableBatterySampling -> batterySampling.disableSampling()
            PersistentServiceCommand.RestoreNotification -> notification.scheduleRestore()
            PersistentServiceCommand.StopService -> stopService()
            PersistentServiceCommand.DismissLockScreen -> appLock.dismissLockScreen()
        }
    }
}
