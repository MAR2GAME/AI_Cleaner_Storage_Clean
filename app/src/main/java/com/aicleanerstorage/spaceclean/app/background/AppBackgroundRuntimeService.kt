package com.aicleanerstorage.spaceclean.app.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.feature.applock.service.AppLockServiceCoordinator
import com.aicleanerstorage.spaceclean.feature.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatterySamplingCoordinator
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BackgroundBatterySamplingPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject
import java.util.concurrent.atomic.AtomicBoolean

class AppBackgroundRuntimeService : Service() {
    private val batteryHistorySampler: BatteryHistorySampler by inject()
    private val backgroundBatterySamplingPreferences: BackgroundBatterySamplingPreferences by inject()
    private val appLockRepository: AppLockRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var batterySampling: BatterySamplingCoordinator
    private lateinit var appLock: AppLockServiceCoordinator
    private lateinit var notification: ForegroundServiceNotificationController
    private lateinit var commandDispatcher: PersistentServiceCommandDispatcher
    private lateinit var commandReceiver: PersistentServiceCommandReceiver

    override fun onCreate() {
        super.onCreate()
        batterySampling =
            BatterySamplingCoordinator(
                sampler = batteryHistorySampler,
                preferences = backgroundBatterySamplingPreferences,
                hasNotificationPermission = { AppConfig.hasPostNotificationsPermission(this) },
            )
        appLock = AppLockServiceCoordinator(this, appLockRepository, serviceScope)
        notification = ForegroundServiceNotificationController(this, serviceScope, stopRequested::get)
        commandDispatcher =
            PersistentServiceCommandDispatcher(
                appLock = appLock,
                batterySampling = batterySampling,
                notification = notification,
                stopService = ::requestStop,
            )
        commandReceiver = PersistentServiceCommandReceiver(this, commandDispatcher::dispatch)

        notification.initialize()
        _isRunning.set(true)
        startInFlight.set(false)
        if (stopRequested.get()) {
            requestStop()
            return
        }

        batterySampling.syncSamplingState()
        runCatching { commandReceiver.register() }
        runCatching { appLock.registerPackageEvents() }
        appLock.syncMonitoringState()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        notification.ensureForeground()
        _isRunning.set(true)
        startInFlight.set(false)

        val command = persistentServiceCommand(intent?.action) ?: PersistentServiceCommand.Start
        if (command == PersistentServiceCommand.StopService) {
            commandDispatcher.dispatch(command)
            return START_NOT_STICKY
        }
        if (
            command == PersistentServiceCommand.Start ||
            command == PersistentServiceCommand.EnableMonitoring ||
            command == PersistentServiceCommand.DisableMonitoring
        ) {
            stopRequested.set(false)
        }
        commandDispatcher.dispatch(command)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        batterySampling.disableSampling()
        appLock.destroy()
        commandReceiver.unregister()
        notification.destroy()
        serviceScope.cancel()
        _isRunning.set(false)
        startInFlight.set(false)
        stopRequested.set(false)
        super.onDestroy()
    }

    private fun requestStop() {
        stopRequested.set(true)
        appLock.disableMonitoring()
        batterySampling.disableSampling()
        notification.stopForeground()
        stopSelf()
    }

    companion object {
        val isRunning: Boolean get() = _isRunning.get()
        private val _isRunning = AtomicBoolean(false)
        private val startInFlight = AtomicBoolean(false)
        private val stopRequested = AtomicBoolean(false)
        private val mainHandler = Handler(Looper.getMainLooper())

        private const val ACTION_START = PersistentServiceActions.START
        const val ACTION_ENABLE_MONITORING = PersistentServiceActions.ENABLE_MONITORING
        const val ACTION_DISABLE_MONITORING = PersistentServiceActions.DISABLE_MONITORING
        private const val ACTION_ENABLE_BATTERY_SAMPLING = PersistentServiceActions.ENABLE_BATTERY_SAMPLING
        private const val ACTION_DISABLE_BATTERY_SAMPLING = PersistentServiceActions.DISABLE_BATTERY_SAMPLING
        private const val ACTION_STOP_SERVICE = PersistentServiceActions.STOP_SERVICE
        const val ACTION_PASSWORD_SUCCESS = PersistentServiceActions.PASSWORD_SUCCESS
        const val ACTION_LOCK_SCREEN_CANCELLED = PersistentServiceActions.LOCK_SCREEN_CANCELLED

        const val FOREGROUND_NOTIFICATION_ID = 17
        private const val START_IN_FLIGHT_RESET_MS = 8_000L

        fun start(context: Context) {
            val appContext = context.applicationContext
            stopRequested.set(false)
            val intent = Intent(appContext, AppBackgroundRuntimeService::class.java).apply { action = ACTION_START }
            if (_isRunning.get()) {
                sendCommandBroadcast(appContext, ACTION_START)
                return
            }
            startForegroundCompat(appContext, intent)
        }

        fun enableMonitoring(context: Context) {
            val appContext = context.applicationContext
            stopRequested.set(false)
            val intent =
                Intent(appContext, AppBackgroundRuntimeService::class.java).apply {
                    action = ACTION_ENABLE_MONITORING
                }
            if (_isRunning.get()) {
                sendCommandBroadcast(appContext, ACTION_ENABLE_MONITORING)
                return
            }
            startForegroundCompat(appContext, intent)
        }

        fun disableMonitoring(context: Context) {
            val appContext = context.applicationContext
            stopRequested.set(false)
            sendCommandBroadcast(appContext, ACTION_DISABLE_MONITORING)
        }

        fun enableBatterySampling(context: Context) {
            val appContext = context.applicationContext
            stopRequested.set(false)
            val intent =
                Intent(appContext, AppBackgroundRuntimeService::class.java).apply {
                    action = ACTION_ENABLE_BATTERY_SAMPLING
                }
            if (_isRunning.get()) {
                sendCommandBroadcast(appContext, ACTION_ENABLE_BATTERY_SAMPLING)
                return
            }
            startForegroundCompat(appContext, intent)
        }

        fun disableBatterySampling(context: Context) {
            val appContext = context.applicationContext
            if (_isRunning.get()) {
                sendCommandBroadcast(appContext, ACTION_DISABLE_BATTERY_SAMPLING)
            }
        }

        fun stop(context: Context) {
            val appContext = context.applicationContext
            stopRequested.set(true)
            sendCommandBroadcast(appContext, ACTION_STOP_SERVICE)
            if (_isRunning.get()) {
                runCatching {
                    appContext.stopService(Intent(appContext, AppBackgroundRuntimeService::class.java))
                }
            }
        }

        private fun startForegroundCompat(
            appContext: Context,
            intent: Intent,
        ) {
            if (!startInFlight.compareAndSet(false, true)) return
            val started =
                runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(appContext, intent)
                    } else {
                        appContext.startService(intent)
                    }
                }.isSuccess
            if (!started) {
                startInFlight.set(false)
                return
            }
            mainHandler.postDelayed(
                { if (!_isRunning.get()) startInFlight.set(false) },
                START_IN_FLIGHT_RESET_MS,
            )
        }

        private fun sendCommandBroadcast(
            appContext: Context,
            action: String,
        ) {
            runCatching {
                appContext.sendBroadcast(Intent(action).setPackage(appContext.packageName))
            }
        }
    }
}
