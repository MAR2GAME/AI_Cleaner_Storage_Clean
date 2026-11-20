package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository

internal class SettingsRepositoryImpl(private val preferences: PreferencesUtil) : SettingsRepository {
    override fun readTemperatureUnit(): String = preferences.getString(KEY_TEMPERATURE_UNIT, "C")

    override fun saveTemperatureUnit(unit: String) {
        preferences.edit { putString(KEY_TEMPERATURE_UNIT, unit) }
    }

    override fun readLastAutoRatePromptAt(): Long = preferences.getLong(KEY_LAST_AUTO_RATE_PROMPT_AT, 0L)

    override fun saveLastAutoRatePromptAt(timestampMillis: Long) {
        preferences.edit { putLong(KEY_LAST_AUTO_RATE_PROMPT_AT, timestampMillis) }
    }

    override fun hasShownNotificationCleanerExitPrompt(): Boolean =
        preferences.getBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, false)

    override fun saveNotificationCleanerExitPromptShown() {
        preferences.edit { putBoolean(KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN, true) }
    }

    override fun isOnboardingScanCompleted(): Boolean =
        preferences.getBoolean(KEY_ONBOARDING_SCAN_COMPLETED, false)

    override fun markOnboardingScanCompleted() {
        preferences.edit { putBoolean(KEY_ONBOARDING_SCAN_COMPLETED, true) }
    }

    override fun isDuplicateFilesWarningAccepted(): Boolean =
        preferences.getBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, false)

    override fun acceptDuplicateFilesWarning() {
        preferences.edit(commit = true) { putBoolean(KEY_DUPLICATE_FILES_WARNING_ACCEPTED, true) }
    }

    override fun isBackgroundBatterySamplingEnabled(): Boolean =
        preferences.getBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, false)

    override fun enableBackgroundBatterySampling() {
        preferences.edit { putBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, true) }
    }
}

private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
private const val KEY_LAST_AUTO_RATE_PROMPT_AT = "last_auto_rate_prompt_at"
private const val KEY_NOTIFICATION_CLEANER_EXIT_PROMPT_SHOWN = "notification_bar_exit_prompt_shown"
private const val KEY_ONBOARDING_SCAN_COMPLETED = "onboarding_scan_completed"
private const val KEY_DUPLICATE_FILES_WARNING_ACCEPTED = "duplicate_files_warning_accepted"
private const val KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED = "background_battery_sampling_enabled"
