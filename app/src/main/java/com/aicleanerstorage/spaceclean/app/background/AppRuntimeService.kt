package com.aicleanerstorage.spaceclean.app.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.rule.AppLockMonitoringRule
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
    private val batteryHistorySampler: BatteryHistoryRecorder by inject()
    private val settingsRepository: AppSettingsRepository by inject()

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
        if (!shouldRunRuntime(this, appLockRepository, settingsRepository)) stopSelf()
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
            settingsRepository: AppSettingsRepository,
        ) {
            if (shouldRunRuntime(context, appLockRepository, settingsRepository)) {
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
    context: Context,
    appLockRepository: AppLockRepository,
    settingsRepository: AppSettingsRepository,
): Boolean =
    AppLockMonitoringRule.shouldMonitorApps(
        repository = appLockRepository,
        hasOverlayPermission = AppPermission.Overlay.isGranted(context),
        hasUsageAccess = AppPermission.UsageAccess.isGranted(context),
    ) ||
        settingsRepository.isBackgroundBatterySamplingEnabled()
