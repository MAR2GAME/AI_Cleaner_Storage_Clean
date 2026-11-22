package com.aicleanerstorage.spaceclean.data.repository

import android.content.ComponentName
import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.notificationcleaner.NotificationCleanerDataSource
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.model.notification.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.notification.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.model.notification.PostedNotificationModel
import com.aicleanerstorage.spaceclean.domain.port.NotificationCleanerRepository

/** 用通知监听服务与偏好存储实现通知拦截、记录和会话快照。 */
internal class DefaultNotificationCleanerRepository(
    context: Context,
    private val listener: ComponentName?,
    private val retainContent: Boolean,
    preferences: PreferencesDataSource,
) : NotificationCleanerRepository {
    private val appContext = context.applicationContext
    private val dataSource = NotificationCleanerDataSource(preferences)

    init {
        if (!retainContent) dataSource.clearStoredContent(appContext)
    }

    override fun shouldBlock(packageName: String) = dataSource.shouldBlock(appContext, listener, packageName)

    override fun recordBlockedNotification(posted: PostedNotificationModel) =
        dataSource.recordBlockedNotification(
            appContext,
            if (retainContent) posted else posted.copy(title = "", text = ""),
        )

    override fun hasNotificationListenerAccess(): Boolean = dataSource.hasNotificationListenerAccess(appContext, listener)

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
        val blocked = dataSource.blockedNotifications(appContext)
        return NotificationCleanerSnapshotModel(
            hasAccess = true,
            enabled = dataSource.isEnabled(appContext, listener),
            blockedCount = blocked.size,
            blockedCountsByPackage = blocked.groupingBy { it.packageName }.eachCount(),
            blockedNotifications = blocked,
            apps = if (includeApps) notificationApps() else emptyList(),
            selectedPackages = selectedNotificationPackages(),
        )
    }

    override fun setNotificationBlockingEnabled(enabled: Boolean) {
        dataSource.setEnabled(appContext, enabled)
    }

    override fun clearBlockedNotifications(): Int = dataSource.clearBlockedNotifications(appContext)

    override fun selectedNotificationPackages(): Set<String> = dataSource.selectedPackages(appContext)

    override fun notificationApps(): List<BlockableNotificationAppModel> = dataSource.apps(appContext)

    override fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    ) {
        dataSource.setPackageSelected(appContext, packageName, selected)
    }
}
