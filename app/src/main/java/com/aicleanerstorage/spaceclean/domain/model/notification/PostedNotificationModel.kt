package com.aicleanerstorage.spaceclean.domain.model.notification

/** 系统刚发布的一条通知，用于判断是否拦截。 */
data class PostedNotificationModel(
    val packageName: String,
    val title: String,
    val text: String,
    val postTimeMillis: Long,
    val hasContentIntent: Boolean,
)
