package com.aicleanerstorage.spaceclean.app.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aicleanerstorage.spaceclean.R

/**
 * Owns the single foreground notification of [AppRuntimeService], including the
 * channel it lives in.
 *
 * No watchdog and no wake lock: if the notification disappears the service has
 * already lost its purpose, and re-posting it forever only costs battery.
 */
internal class ForegroundNotification(
    private val service: Service,
) {
    private var channelReady = false

    fun show() {
        ensureChannel()
        val notification = build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            service.startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            service.startForeground(NOTIFICATION_ID, notification)
        }
    }

    fun stop() {
        service.stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    private fun ensureChannel() {
        if (channelReady) return
        val manager = service.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                service.getString(R.string.running_in_background),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = service.getString(R.string.running_in_background)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
        )
        channelReady = true
    }

    private fun build(): Notification =
        NotificationCompat
            .Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_n_notification_cleaner)
            .setContentTitle(service.getString(R.string.app_name))
            .setContentText(service.getString(R.string.running_in_background))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

    companion object {
        const val NOTIFICATION_ID = 17
        private const val CHANNEL_ID = "storage_clean_persistent"
    }
}
