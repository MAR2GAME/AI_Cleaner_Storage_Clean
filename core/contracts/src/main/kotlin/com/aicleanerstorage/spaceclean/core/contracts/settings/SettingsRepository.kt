package com.aicleanerstorage.spaceclean.core.contracts.settings

interface SettingsRepository {
    fun readTemperatureUnit(): String

    fun saveTemperatureUnit(unit: String)

    fun readLastAutoRatePromptAt(): Long

    fun saveLastAutoRatePromptAt(timestampMillis: Long)

    fun hasShownNotificationCleanerExitPrompt(): Boolean

    fun saveNotificationCleanerExitPromptShown()

}
