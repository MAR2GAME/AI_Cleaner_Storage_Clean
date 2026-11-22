package com.aicleanerstorage.spaceclean.domain.model.notification

/** 通知清理页一次读取的完整状态。 */
data class NotificationCleanerSnapshotModel(
    val hasAccess: Boolean,
    val enabled: Boolean,
    val blockedCount: Int,
    val blockedCountsByPackage: Map<String, Int>,
    val blockedNotifications: List<BlockedNotificationModel>,
    val apps: List<BlockableNotificationAppModel>,
    val selectedPackages: Set<String>,
)
