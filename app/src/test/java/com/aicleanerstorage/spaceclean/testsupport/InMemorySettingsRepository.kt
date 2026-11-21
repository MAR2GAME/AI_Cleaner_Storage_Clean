package com.aicleanerstorage.spaceclean.testsupport

import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository

internal class InMemorySettingsRepository(
    private var backgroundSampling: Boolean = false,
) : SettingsRepository {
    private var temperatureUnit = "C"
    private var lastRatingPrompt = 0L
    private var lastNotificationPermissionPrompt = 0L
    private var exitPromptShown = false
    private var onboardingCompleted = false
    private var duplicateWarningAccepted = false

    override fun readTemperatureUnit() = temperatureUnit

    override fun saveTemperatureUnit(unit: String) {
        temperatureUnit = unit
    }

    override fun readLastAutoRatePromptAt() = lastRatingPrompt

    override fun saveLastAutoRatePromptAt(timestampMillis: Long) {
        lastRatingPrompt = timestampMillis
    }

    override fun readLastNotificationPermissionPromptAt() = lastNotificationPermissionPrompt

    override fun saveLastNotificationPermissionPromptAt(timestampMillis: Long) {
        lastNotificationPermissionPrompt = timestampMillis
    }

    override fun hasShownNotificationCleanerExitPrompt() = exitPromptShown

    override fun saveNotificationCleanerExitPromptShown() {
        exitPromptShown = true
    }

    override fun isOnboardingScanCompleted() = onboardingCompleted

    override fun markOnboardingScanCompleted() {
        onboardingCompleted = true
    }

    override fun isDuplicateFilesWarningAccepted() = duplicateWarningAccepted

    override fun acceptDuplicateFilesWarning() {
        duplicateWarningAccepted = true
    }

    override fun isBackgroundBatterySamplingEnabled() = backgroundSampling

    override fun enableBackgroundBatterySampling() {
        backgroundSampling = true
    }
}
