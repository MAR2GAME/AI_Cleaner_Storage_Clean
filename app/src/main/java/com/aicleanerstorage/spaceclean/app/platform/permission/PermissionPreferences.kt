package com.aicleanerstorage.spaceclean.app.platform.permission

import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil

/** 通知提示频率；权限状态本身不落盘，随时向系统复检。 */
class PermissionPreferences(
    private val preferences: PreferencesUtil,
) {
    fun readLastNotificationPermissionCustomPromptAt(): Long =
        preferences.getLong(KEY_LAST_NOTIFICATION_PERMISSION_CUSTOM_PROMPT_AT, 0L)

    fun saveLastNotificationPermissionCustomPromptAt(timestampMillis: Long) {
        preferences.edit { putLong(KEY_LAST_NOTIFICATION_PERMISSION_CUSTOM_PROMPT_AT, timestampMillis) }
    }
}

private const val KEY_LAST_NOTIFICATION_PERMISSION_CUSTOM_PROMPT_AT =
    "last_notification_permission_custom_prompt_at"
