package com.aicleanerstorage.spaceclean.testsupport

import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository

internal class InMemoryAppSettingsRepository(
    private var backgroundSampling: Boolean = false,
) : AppSettingsRepository {
    private var temperatureUnit = "C"
    private var lastRatingPrompt = 0L
    private var lastNotificationPermissionPrompt = 0L
    private var exitPromptShown = false
    private var onboardingCompleted = false
    private var firstOnboardingPending = true
    private var secondOnboardingPending = true
    private var duplicateWarningAccepted = false

    override fun readTemperatureUnit() = temperatureUnit

    override fun saveTemperatureUnit(unit: String): Result<Unit> {
        temperatureUnit = unit
        return Result.success(Unit)
    }

    override fun readLastAutoRatePromptAt() = lastRatingPrompt

    override fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit> {
        lastRatingPrompt = timestampMillis
        return Result.success(Unit)
    }

    override fun readLastNotificationPermissionPromptAt() = lastNotificationPermissionPrompt

    override fun saveLastNotificationPermissionPromptAt(timestampMillis: Long): Result<Unit> {
        lastNotificationPermissionPrompt = timestampMillis
        return Result.success(Unit)
    }

    override fun hasShownNotificationCleanerExitPrompt() = exitPromptShown

    override fun saveNotificationCleanerExitPromptShown(): Result<Unit> {
        exitPromptShown = true
        return Result.success(Unit)
    }

    override fun isOnboardingScanCompleted() = onboardingCompleted

    override fun markOnboardingScanCompleted(): Result<Unit> {
        onboardingCompleted = true
        return Result.success(Unit)
    }

    override fun isFirstOnboardingPending() = !onboardingCompleted && firstOnboardingPending

    override fun isSecondOnboardingPending() = !onboardingCompleted && secondOnboardingPending

    override fun markFirstOnboardingCompleted(): Result<Unit> {
        firstOnboardingPending = false
        return Result.success(Unit)
    }

    override fun markSecondOnboardingCompleted(): Result<Unit> {
        secondOnboardingPending = false
        onboardingCompleted = true
        return Result.success(Unit)
    }

    override fun isDuplicateFilesWarningAccepted() = duplicateWarningAccepted

    override fun acceptDuplicateFilesWarning(): Result<Unit> {
        duplicateWarningAccepted = true
        return Result.success(Unit)
    }

    override fun isBackgroundBatterySamplingEnabled() = backgroundSampling

    override fun enableBackgroundBatterySampling(): Result<Unit> {
        backgroundSampling = true
        return Result.success(Unit)
    }
}
