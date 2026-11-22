package com.aicleanerstorage.spaceclean.domain.model.applock

/** 可锁定应用及其当前锁定状态。 */
data class AppLockAppModel(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean,
)
