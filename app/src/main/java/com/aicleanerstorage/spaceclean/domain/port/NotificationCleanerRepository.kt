package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.notification.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.notification.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.model.notification.PostedNotificationModel

interface NotificationCleanerRepository {
    fun shouldBlock(packageName: String): Boolean

    fun recordBlockedNotification(posted: PostedNotificationModel)

    fun hasNotificationListenerAccess(): Boolean

    fun notificationSnapshot(includeApps: Boolean = true): NotificationCleanerSnapshotModel

    fun setNotificationBlockingEnabled(enabled: Boolean)

    fun clearBlockedNotifications(): Int

    fun selectedNotificationPackages(): Set<String>

    fun notificationApps(): List<BlockableNotificationAppModel>

    fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    )
}
