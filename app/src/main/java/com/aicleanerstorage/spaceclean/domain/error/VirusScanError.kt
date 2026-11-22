package com.aicleanerstorage.spaceclean.domain.error

/**
 * 病毒扫描的失败词汇。
 *
 * 适配器把 SDK 的 int code / message 收口到这里；页面只按分支决定
 * “配置错误、可重试失败或普通失败”，不再依赖 SDK 的错误码。
 */
sealed interface VirusScanError {
    /** API key 未配置，扫描没有启动；页面展示配置错误，不应重试。 */
    data object NotConfigured : VirusScanError

    /**
     * 扫描引擎返回错误或启动失败。
     *
     * @property code SDK 错误码；只用于日志和诊断，页面不按它分支。
     * @property message 可展示给用户的原始信息，可为空。
     */
    data class Engine(
        val code: Int,
        val message: String?,
    ) : VirusScanError
}
