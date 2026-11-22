package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository

/**
 * 用 [PreferencesDataSource] 实现用户配置；写入失败以 Result 返回，不抛出到页面。
 *
 * @param preferences 偏好数据源；业务 key、默认值和写入策略都收在本类。
 */
internal class DefaultAppSettingsRepository(
    private val preferences: PreferencesDataSource,
) : AppSettingsRepository {
    override fun readTemperatureUnit(): String = preferences.getString(KEY_TEMPERATURE_UNIT, "C")

    override fun saveTemperatureUnit(unit: String): Result<Unit> =
        runCatching { preferences.edit { putString(KEY_TEMPERATURE_UNIT, unit) } }

    override fun readLastAutoRatePromptAt(): Long = preferences.getLong(KEY_LAST_AUTO_RATE_PROMPT_AT, 0L)

    override fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit> =
        runCatching { preferences.edit { putLong(KEY_LAST_AUTO_RATE_PROMPT_AT, timestampMillis) } }

    override fun readLastNotificationPermissionPromptAt(): Long =
        preferences.getLong(KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT, 0L)

    override fun saveLastNotificationPermissionPromptAt(timestampMillis: Long): Result<Unit> =
        runCatching { preferences.edit { putLong(KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT, timestampMillis) } }

    override fun hasShownNotificationCleanerExitPrompt(): Boolean =
        preferences.getBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, false)

    override fun saveNotificationCleanerExitPromptShown(): Result<Unit> =
        runCatching { preferences.edit { putBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, true) } }

    override fun isOnboardingScanCompleted(): Boolean = preferences.getBoolean(KEY_ONBOARDING_SCAN_COMPLETED, false)

    override fun markOnboardingScanCompleted(): Result<Unit> =
        runCatching { preferences.edit { putBoolean(KEY_ONBOARDING_SCAN_COMPLETED, true) } }

    override fun isDuplicateFilesWarningAccepted(): Boolean = preferences.getBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, false)

    override fun acceptDuplicateFilesWarning(): Result<Unit> =
        runCatching { preferences.edit(commit = true) { putBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, true) } }

    override fun isBackgroundBatterySamplingEnabled(): Boolean =
        preferences.getBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, false)

    override fun enableBackgroundBatterySampling(): Result<Unit> =
        runCatching { preferences.edit { putBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, true) } }
}

private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
private const val KEY_LAST_AUTO_RATE_PROMPT_AT = "last_auto_rate_prompt_at"
private const val KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT = "last_notification_permission_custom_prompt_at"
private const val KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN = "notification_bar_exit_prompt_shown"
private const val KEY_ONBOARDING_SCAN_COMPLETED = "onboarding_scan_completed"
private const val KEY_DUPLICATE_FILES_WARNING_ACCEPTED = "duplicate_files_warning_accepted"
private const val KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED = "background_battery_sampling_enabled"
