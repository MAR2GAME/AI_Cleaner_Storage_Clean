package com.aicleanerstorage.spaceclean.domain.model.notification

/** 可在通知清理页选择拦截的应用。 */
data class BlockableNotificationAppModel(
    val appName: String,
    val packageName: String,
)
