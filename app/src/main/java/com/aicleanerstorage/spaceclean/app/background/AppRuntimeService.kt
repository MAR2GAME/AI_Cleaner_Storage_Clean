package com.aicleanerstorage.spaceclean.app.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.data.applock.AppLockMonitoringRule
import com.aicleanerstorage.spaceclean.data.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.data.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject

/**
 * The single foreground service of the app.
 *
 * It owns the process while App Lock or background battery sampling needs it and
 * re-evaluates that need through [sync]; nothing else decides its lifetime.
 */
class AppRuntimeService : Service() {
    private val appLockRepository: AppLockRepository by inject()
    private val appUsageRepository: AppUsageRepository by inject()
    private val batteryHistorySampler: BatteryHistorySampler by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var notification: ForegroundNotification
    private lateinit var appLock: AppLockMonitor
    private lateinit var packageEvents: AppLockPackageEvents

    override fun onCreate() {
        super.onCreate()
        notification = ForegroundNotification(this)
        notification.show()

        appLock = AppLockMonitor(this, appLockRepository, appUsageRepository, serviceScope)
        packageEvents = AppLockPackageEvents(this, appLockRepository, ::sync)
        packageEvents.register()
        sync()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        notification.show()
        sync()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        packageEvents.unregister()
        appLock.stop()
        runCatching { batteryHistorySampler.stop(BatteryHistoryOwner.Service) }
        notification.stop()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun sync() {
        appLock.sync()
        syncBatterySampling()
        if (!shouldRunRuntime(appLockRepository, settingsRepository)) stopSelf()
    }

    /**
     * Background history follows the user's switch only. A disabled notification
     * hides the foreground notification but must not stop the sampling.
     */
    private fun syncBatterySampling() {
        val enabled = settingsRepository.isBackgroundBatterySamplingEnabled()
        runCatching {
            if (enabled) {
                batteryHistorySampler.start(BatteryHistoryOwner.Service)
            } else {
                batteryHistorySampler.stop(BatteryHistoryOwner.Service)
            }
        }
    }

    companion object {
        /**
         * Brings the service in line with the current settings. Callers never
         * start or stop it directly, so a no-op change never flashes the
         * foreground notification.
         */
        fun reconcile(
            context: Context,
            appLockRepository: AppLockRepository,
            settingsRepository: SettingsRepository,
        ) {
            if (shouldRunRuntime(appLockRepository, settingsRepository)) {
                start(context)
            } else {
                stop(context)
            }
        }

        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, AppRuntimeService::class.java),
            )
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AppRuntimeService::class.java))
        }
    }
}

/** The only two reasons for the runtime to exist. */
private fun shouldRunRuntime(
    appLockRepository: AppLockRepository,
    settingsRepository: SettingsRepository,
): Boolean =
    AppLockMonitoringRule.shouldMonitorApps(appLockRepository) ||
        settingsRepository.isBackgroundBatterySamplingEnabled()
