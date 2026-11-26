package com.aicleanerstorage.spaceclean.domain.port

/**
 * 应用级用户配置与提示节流标记。
 *
 * 读方法直接返回当前值；写方法返回 failure 时，调用方应保留已展示的状态，
 * 不要假设配置已经落盘。
 */
interface AppSettingsRepository {
    /**
     * 读取温度单位。
     *
     * @return 当前单位；未设置时返回默认单位。
     */
    fun readTemperatureUnit(): String

    /**
     * 保存温度单位。
     *
     * @param unit 目标单位。
     * @return 写入结果；failure 表示持久化失败。
     */
    fun saveTemperatureUnit(unit: String): Result<Unit>

    /**
     * 读取上次自动评分提示时间。
     *
     * @return 时间戳；未提示过时返回 0。
     */
    fun readLastAutoRatePromptAt(): Long

    /**
     * 保存上次自动评分提示时间。
     *
     * @param timestampMillis 提示发生的时间戳。
     * @return 写入结果；failure 表示持久化失败。
     */
    fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit>

    /**
     * 读取上次通知权限提示时间。
     *
     * @return 时间戳；未提示过时返回 0。
     */
    fun readLastNotificationPermissionPromptAt(): Long

    /**
     * 保存上次通知权限提示时间。
     *
     * @param timestampMillis 提示发生的时间戳。
     * @return 写入结果；failure 表示持久化失败。
     */
    fun saveLastNotificationPermissionPromptAt(timestampMillis: Long): Result<Unit>

    /**
     * 判断是否展示过通知清理退出提示。
     *
     * @return true 表示已经展示过，后续不再自动弹出。
     */
    fun hasShownNotificationCleanerExitPrompt(): Boolean

    /**
     * 标记通知清理退出提示已展示。
     *
     * @return 写入结果；failure 表示持久化失败。
     */
    fun saveNotificationCleanerExitPromptShown(): Result<Unit>

    /**
     * 判断 onboarding 扫描是否完成。
     *
     * @return true 表示已完成，可直接进入首页。
     */
    fun isOnboardingScanCompleted(): Boolean

    /**
     * 标记 onboarding 扫描完成。
     *
     * @return 写入结果；failure 表示持久化失败。
     */
    fun markOnboardingScanCompleted(): Result<Unit>

    /**
     * 判断用户是否完成过至少一次有效清理。
     *
     * @return true 表示通知权限提示可以使用“清理后”场景标记。
     */
    fun hasCompletedCleanup(): Boolean

    /**
     * 记录一次有效清理完成。
     *
     * @return 写入结果；failure 表示持久化失败。
     */
    fun markCleanupCompleted(): Result<Unit>

    /**
     * 判断重复文件删除警告是否已接受。
     *
     * @return true 表示用户已接受风险提示。
     */
    fun isDuplicateFilesWarningAccepted(): Boolean

    /**
     * 记录重复文件删除警告已接受。
     *
     * @return 写入结果；failure 表示持久化失败。
     */
    fun acceptDuplicateFilesWarning(): Result<Unit>

    /**
     * 判断后台电池采样是否开启。
     *
     * @return true 表示已允许后台采样。
     */
    fun isBackgroundBatterySamplingEnabled(): Boolean

    /**
     * 开启后台电池采样。
     *
     * @return 写入结果；failure 表示持久化失败。
     */
    fun enableBackgroundBatterySampling(): Result<Unit>
}
