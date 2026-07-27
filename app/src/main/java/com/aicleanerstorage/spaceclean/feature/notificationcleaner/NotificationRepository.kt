package com.aicleanerstorage.spaceclean.feature.notificationcleaner

import com.aicleanerstorage.spaceclean.feature.notificationcleaner.BlockableNotificationApp

data class NotificationCleanerSnapshot(
    val hasAccess: Boolean,
    val enabled: Boolean,
    val blockedCount: Int,
    val blockedCountsByPackage: Map<String, Int>,
    val apps: List<BlockableNotificationApp>,
    val selectedPackages: Set<String>,
)

interface NotificationRepository {
    fun hasNotificationListenerAccess(): Boolean

    fun notificationSnapshot(): NotificationCleanerSnapshot

    fun isNotificationBlockingEnabled(): Boolean

    fun setNotificationBlockingEnabled(enabled: Boolean)

    fun blockedNotificationCount(): Int

    fun blockedNotificationCountsByPackage(): Map<String, Int>

    fun selectedNotificationPackages(): Set<String>

    fun notificationApps(): List<BlockableNotificationApp>

    fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    )

}
