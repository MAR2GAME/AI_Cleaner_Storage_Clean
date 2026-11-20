package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.NotificationDataSource
import com.aicleanerstorage.spaceclean.domain.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.model.PostedNotificationModel
import com.aicleanerstorage.spaceclean.domain.port.NotificationRepository

internal class NotificationRepositoryImpl(
    context: Context,
) : NotificationRepository {
    private val appContext = context.applicationContext

    override fun shouldBlock(packageName: String) = NotificationDataSource.shouldBlock(appContext, packageName)

    override fun recordBlockedNotification(posted: PostedNotificationModel) = NotificationDataSource.recordBlockedNotification(appContext, posted)

    override fun hasNotificationListenerAccess(): Boolean = NotificationDataSource.hasNotificationListenerAccess(appContext)

    override fun notificationSnapshot(includeApps: Boolean): NotificationCleanerSnapshotModel {
        val hasAccess = hasNotificationListenerAccess()
        if (!hasAccess) {
            return NotificationCleanerSnapshotModel(
                hasAccess = false,
                enabled = false,
                blockedCount = 0,
                blockedCountsByPackage = emptyMap(),
                blockedNotifications = emptyList(),
                apps = emptyList(),
                selectedPackages = emptySet(),
            )
        }
        val blocked = NotificationDataSource.blockedNotifications(appContext)
        return NotificationCleanerSnapshotModel(
            hasAccess = true,
            enabled = NotificationDataSource.isEnabled(appContext),
            blockedCount = blocked.size,
            blockedCountsByPackage = blocked.groupingBy { it.packageName }.eachCount(),
            blockedNotifications = blocked,
            apps = if (includeApps) notificationApps() else emptyList(),
            selectedPackages = selectedNotificationPackages(),
        )
    }

    override fun setNotificationBlockingEnabled(enabled: Boolean) {
        NotificationDataSource.setEnabled(appContext, enabled)
    }

    override fun clearBlockedNotifications(): Int = NotificationDataSource.clearBlockedNotifications(appContext)

    override fun selectedNotificationPackages(): Set<String> = NotificationDataSource.selectedPackages(appContext)

    override fun notificationApps(): List<BlockableNotificationAppModel> = NotificationDataSource.apps(appContext)

    override fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    ) {
        NotificationDataSource.setPackageSelected(appContext, packageName, selected)
    }
}
