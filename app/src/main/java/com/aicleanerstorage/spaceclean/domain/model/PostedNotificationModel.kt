package com.aicleanerstorage.spaceclean.domain.model

data class PostedNotificationModel(
    val packageName: String,
    val title: String,
    val text: String,
    val postTimeMillis: Long,
    val hasContentIntent: Boolean,
)
