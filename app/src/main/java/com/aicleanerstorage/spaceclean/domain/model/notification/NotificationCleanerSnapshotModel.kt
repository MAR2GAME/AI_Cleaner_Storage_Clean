package com.aicleanerstorage.spaceclean.domain.model.notification

data class NotificationCleanerSnapshotModel(
    val hasAccess: Boolean,
    val enabled: Boolean,
    val blockedCount: Int,
    val blockedCountsByPackage: Map<String, Int>,
    val blockedNotifications: List<BlockedNotificationModel>,
    val apps: List<BlockableNotificationAppModel>,
    val selectedPackages: Set<String>,
)
