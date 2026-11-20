package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.model.PostedNotificationModel

interface NotificationRepository {
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
