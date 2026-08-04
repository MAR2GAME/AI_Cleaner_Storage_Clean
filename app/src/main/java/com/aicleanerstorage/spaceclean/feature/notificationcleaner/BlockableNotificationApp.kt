package com.aicleanerstorage.spaceclean.feature.notificationcleaner

data class BlockableNotificationApp(
    val appName: String,
    val packageName: String,
)

data class BlockedNotification(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val postTimeMillis: Long,
    val hasLaunchIntent: Boolean,
)
