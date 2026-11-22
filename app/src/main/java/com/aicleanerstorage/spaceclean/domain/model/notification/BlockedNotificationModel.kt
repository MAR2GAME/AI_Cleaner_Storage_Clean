package com.aicleanerstorage.spaceclean.domain.model.notification

data class BlockedNotificationModel(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val postTimeMillis: Long,
    val hasLaunchIntent: Boolean,
)
