package com.aicleanerstorage.spaceclean.core.contracts.applock

data class AppLockApp(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean,
)
