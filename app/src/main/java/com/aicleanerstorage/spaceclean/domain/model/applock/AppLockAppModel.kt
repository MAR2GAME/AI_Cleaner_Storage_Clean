package com.aicleanerstorage.spaceclean.domain.model.applock

data class AppLockAppModel(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean,
)
