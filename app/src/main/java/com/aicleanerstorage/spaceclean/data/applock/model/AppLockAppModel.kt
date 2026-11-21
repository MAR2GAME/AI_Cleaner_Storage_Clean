package com.aicleanerstorage.spaceclean.data.applock.model

data class AppLockAppModel(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean,
)
