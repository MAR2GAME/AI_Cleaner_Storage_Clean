package com.aicleanerstorage.spaceclean.data.repository

import android.content.SharedPreferences
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import java.io.IOException

/**
 * 用 [PreferencesDataSource] 实现用户配置；写入失败以 Result 返回，不抛出到页面。
 *
 * @param preferences 偏好数据源；业务 key、默认值和写入策略都收在本类。
 */
internal class DefaultAppSettingsRepository(
    private val preferences: PreferencesDataSource,
) : AppSettingsRepository {
    override fun readTemperatureUnit(): String = preferences.getString(KEY_TEMPERATURE_UNIT, "C")

    override fun saveTemperatureUnit(unit: String): Result<Unit> = save { putString(KEY_TEMPERATURE_UNIT, unit) }

    override fun readLastAutoRatePromptAt(): Long = preferences.getLong(KEY_LAST_AUTO_RATE_PROMPT_AT, 0L)

    override fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit> =
        save { putLong(KEY_LAST_AUTO_RATE_PROMPT_AT, timestampMillis) }

    override fun readLastNotificationPermissionPromptAt(): Long = preferences.getLong(KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT, 0L)

    override fun saveLastNotificationPermissionPromptAt(timestampMillis: Long): Result<Unit> =
        save { putLong(KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT, timestampMillis) }

    override fun hasShownNotificationCleanerExitPrompt(): Boolean =
        preferences.getBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, false)

    override fun saveNotificationCleanerExitPromptShown(): Result<Unit> =
        save { putBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, true) }

    override fun isOnboardingScanCompleted(): Boolean = preferences.getBoolean(KEY_ONBOARDING_SCAN_COMPLETED, false)

    override fun markOnboardingScanCompleted(): Result<Unit> = save { putBoolean(KEY_ONBOARDING_SCAN_COMPLETED, true) }

    override fun isFirstOnboardingPending(): Boolean =
        !isOnboardingScanCompleted() && preferences.getBoolean(KEY_FIRST_ONBOARDING_PENDING, true)

    override fun isSecondOnboardingPending(): Boolean =
        !isOnboardingScanCompleted() && preferences.getBoolean(KEY_SECOND_ONBOARDING_PENDING, true)

    override fun markFirstOnboardingCompleted(): Result<Unit> =
        save { putBoolean(KEY_FIRST_ONBOARDING_PENDING, false) }

    override fun markSecondOnboardingCompleted(): Result<Unit> =
        save {
            putBoolean(KEY_SECOND_ONBOARDING_PENDING, false)
            putBoolean(KEY_ONBOARDING_SCAN_COMPLETED, true)
        }

    override fun isDuplicateFilesWarningAccepted(): Boolean = preferences.getBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, false)

    override fun acceptDuplicateFilesWarning(): Result<Unit> = save { putBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, true) }

    override fun isBackgroundBatterySamplingEnabled(): Boolean = preferences.getBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, false)

    override fun enableBackgroundBatterySampling(): Result<Unit> = save { putBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, true) }

    private fun save(action: SharedPreferences.Editor.() -> Unit): Result<Unit> =
        if (preferences.editAndCommit(action)) {
            Result.success(Unit)
        } else {
            Result.failure(IOException("Settings write failed"))
        }
}

private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
private const val KEY_LAST_AUTO_RATE_PROMPT_AT = "last_auto_rate_prompt_at"
private const val KEY_LAST_NOTIFICATION_PERMISSION_PROMPT_AT = "last_notification_permission_custom_prompt_at"
private const val KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN = "notification_bar_exit_prompt_shown"
private const val KEY_ONBOARDING_SCAN_COMPLETED = "onboarding_scan_completed"
private const val KEY_FIRST_ONBOARDING_PENDING = "is_first_launch_1"
private const val KEY_SECOND_ONBOARDING_PENDING = "is_second_launch_1"
private const val KEY_DUPLICATE_FILES_WARNING_ACCEPTED = "duplicate_files_warning_accepted"
private const val KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED = "background_battery_sampling_enabled"
