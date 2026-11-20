package com.aicleanerstorage.spaceclean.data.notificationcleaner

import com.aicleanerstorage.spaceclean.data.notificationcleaner.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.data.notificationcleaner.model.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.data.notificationcleaner.model.PostedNotificationModel

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
