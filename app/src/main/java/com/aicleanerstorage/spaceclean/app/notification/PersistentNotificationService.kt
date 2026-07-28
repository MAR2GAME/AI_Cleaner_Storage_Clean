package com.aicleanerstorage.spaceclean.app.notification

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import java.util.concurrent.atomic.AtomicBoolean

class PersistentNotificationService : Service() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.createAllChannels(this)
        showForeground()
        _isRunning.set(true)
        _startInFlight.set(false)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        showForeground()
        _isRunning.set(true)
        _startInFlight.set(false)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (AppConfig.hasPostNotificationsPermission(applicationContext)) {
            start(applicationContext)
        }
    }

    override fun onDestroy() {
        _isRunning.set(false)
        _startInFlight.set(false)
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(): Notification {
        val collapsed = RemoteViews(packageName, R.layout.notification_persistent_shortcuts_compact)
        val expanded = RemoteViews(packageName, R.layout.notification_persistent_shortcuts)
        bindShortcutViews(collapsed)
        bindShortcutViews(expanded)

        return NotificationCompat.Builder(this, NotificationChannelManager.PERSISTENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_cleaner)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.running_in_background))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCustomContentView(collapsed)
            .setCustomBigContentView(expanded)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    private fun bindShortcutViews(views: RemoteViews) {
        views.setOnClickPendingIntent(
            R.id.persistent_shortcut_clean,
            routePendingIntent(AppDestination.JunkClean.route),
        )
        views.setOnClickPendingIntent(
            R.id.persistent_shortcut_file,
            routePendingIntent(AppDestination.HomeFileManager.route),
        )
        views.setOnClickPendingIntent(
            R.id.persistent_shortcut_tools,
            routePendingIntent(AppDestination.HomeToolbox.route),
        )
        views.setOnClickPendingIntent(
            R.id.persistent_shortcut_battery,
            routePendingIntent(AppDestination.BatteryInfo.route),
        )
    }

    private fun routePendingIntent(route: String) =
        android.app.PendingIntent.getActivity(
            this,
            route.hashCode(),
            Intent(this, com.aicleanerstorage.spaceclean.app.MainActivity::class.java).apply {
                putExtra("Route", route)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
        )

    companion object {
        private const val NOTIFICATION_ID = 2001
        private val _isRunning = AtomicBoolean(false)
        private val _startInFlight = AtomicBoolean(false)

        fun refresh(context: Context) {
            if (AppConfig.hasPostNotificationsPermission(context)) {
                start(context)
            } else {
                stop(context)
            }
        }

        fun start(context: Context) {
            if (_isRunning.get() || !_startInFlight.compareAndSet(false, true)) return
            val appContext = context.applicationContext
            val intent = Intent(appContext, PersistentNotificationService::class.java)
            val started =
                runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(appContext, intent)
                    } else {
                        appContext.startService(intent)
                    }
                }.isSuccess
            if (!started) {
                _startInFlight.set(false)
            }
        }

        fun stop(context: Context) {
            val appContext = context.applicationContext
            _startInFlight.set(false)
            runCatching {
                appContext.stopService(Intent(appContext, PersistentNotificationService::class.java))
            }
        }
    }
}
