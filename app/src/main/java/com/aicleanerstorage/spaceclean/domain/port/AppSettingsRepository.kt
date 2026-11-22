package com.aicleanerstorage.spaceclean.domain.port

/**
 * 应用级用户配置与提示节流标记。
 *
 * 读方法直接返回当前值；写方法返回 failure，调用方保留已展示的状态并选择是否提示。
 */
interface AppSettingsRepository {
    fun readTemperatureUnit(): String

    fun saveTemperatureUnit(unit: String): Result<Unit>

    fun readLastAutoRatePromptAt(): Long

    fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit>

    fun readLastNotificationPermissionPromptAt(): Long

    fun saveLastNotificationPermissionPromptAt(timestampMillis: Long): Result<Unit>

    fun hasShownNotificationCleanerExitPrompt(): Boolean

    fun saveNotificationCleanerExitPromptShown(): Result<Unit>

    fun isOnboardingScanCompleted(): Boolean

    fun markOnboardingScanCompleted(): Result<Unit>

    fun isDuplicateFilesWarningAccepted(): Boolean

    fun acceptDuplicateFilesWarning(): Result<Unit>

    fun isBackgroundBatterySamplingEnabled(): Boolean

    fun enableBackgroundBatterySampling(): Result<Unit>
}
