package com.aicleanerstorage.spaceclean.data.notificationcleaner.internal

import android.content.ComponentName
import android.content.Context
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.port.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.PostedNotificationModel

internal class NotificationCleanerRepositoryImpl(
    context: Context,
    private val listener: ComponentName?,
    private val retainContent: Boolean,
) : NotificationCleanerRepository {
    private val appContext = context.applicationContext

    init {
        if (!retainContent) NotificationCleanerDataSource.clearStoredContent(appContext)
    }

    override fun shouldBlock(packageName: String) = NotificationCleanerDataSource.shouldBlock(appContext, listener, packageName)

    override fun recordBlockedNotification(posted: PostedNotificationModel) =
        NotificationCleanerDataSource.recordBlockedNotification(
            appContext,
            if (retainContent) posted else posted.copy(title = "", text = ""),
        )

    override fun hasNotificationListenerAccess(): Boolean =
        NotificationCleanerDataSource.hasNotificationListenerAccess(appContext, listener)

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
        val blocked = NotificationCleanerDataSource.blockedNotifications(appContext)
        return NotificationCleanerSnapshotModel(
            hasAccess = true,
            enabled = NotificationCleanerDataSource.isEnabled(appContext, listener),
            blockedCount = blocked.size,
            blockedCountsByPackage = blocked.groupingBy { it.packageName }.eachCount(),
            blockedNotifications = blocked,
            apps = if (includeApps) notificationApps() else emptyList(),
            selectedPackages = selectedNotificationPackages(),
        )
    }

    override fun setNotificationBlockingEnabled(enabled: Boolean) {
        NotificationCleanerDataSource.setEnabled(appContext, enabled)
    }

    override fun clearBlockedNotifications(): Int = NotificationCleanerDataSource.clearBlockedNotifications(appContext)

    override fun selectedNotificationPackages(): Set<String> = NotificationCleanerDataSource.selectedPackages(appContext)

    override fun notificationApps(): List<BlockableNotificationAppModel> = NotificationCleanerDataSource.apps(appContext)

    override fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    ) {
        NotificationCleanerDataSource.setPackageSelected(appContext, packageName, selected)
    }
}
