package com.aicleanerstorage.spaceclean.feature.applock

data class AppLockApp(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean,
)
