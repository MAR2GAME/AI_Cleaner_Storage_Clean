package com.aicleanerstorage.spaceclean.data.local.notificationcleaner

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.aicleanerstorage.spaceclean.domain.model.notification.PostedNotificationModel
import com.aicleanerstorage.spaceclean.domain.port.NotificationCleanerRepository
import org.koin.android.ext.android.inject

class StorageCleanNotificationListener : NotificationListenerService() {
    private val repository: NotificationCleanerRepository by inject()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!repository.shouldBlock(sbn.packageName)) return
        repository.recordBlockedNotification(
            PostedNotificationModel(
                packageName = sbn.packageName,
                title = sbn.notification.titleText(),
                text = sbn.notification.bodyText(),
                postTimeMillis = sbn.postTime,
                hasContentIntent = sbn.notification.contentIntent != null,
            ),
        )
        cancelNotification(sbn.key)
    }

    private fun Notification.titleText(): String = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

    private fun Notification.bodyText(): String {
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        if (bigText.isNotBlank()) return bigText
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        if (text.isNotBlank()) return text
        val lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        if (!lines.isNullOrEmpty()) return lines.joinToString("\n") { it.toString() }
        return tickerText?.toString().orEmpty()
    }
}
