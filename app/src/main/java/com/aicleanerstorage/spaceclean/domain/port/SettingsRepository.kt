package com.aicleanerstorage.spaceclean.domain.port

interface SettingsRepository {
    fun readTemperatureUnit(): String

    fun saveTemperatureUnit(unit: String)

    fun readLastAutoRatePromptAt(): Long

    fun saveLastAutoRatePromptAt(timestampMillis: Long)

    fun readLastNotificationPermissionPromptAt(): Long

    fun saveLastNotificationPermissionPromptAt(timestampMillis: Long)

    fun hasShownNotificationCleanerExitPrompt(): Boolean

    fun saveNotificationCleanerExitPromptShown()

    fun isOnboardingScanCompleted(): Boolean

    fun markOnboardingScanCompleted()

    fun isDuplicateFilesWarningAccepted(): Boolean

    fun acceptDuplicateFilesWarning()

    fun isBackgroundBatterySamplingEnabled(): Boolean

    fun enableBackgroundBatterySampling()
}
