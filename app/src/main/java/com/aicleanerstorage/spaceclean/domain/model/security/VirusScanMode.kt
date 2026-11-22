package com.aicleanerstorage.spaceclean.domain.model.security

/** 病毒扫描模式。 */
enum class VirusScanMode {
    /** 快速扫描：已安装应用与常见风险位置。 */
    Quick,

    /** 深度扫描：额外检查文件系统。 */
    Deep,
}
