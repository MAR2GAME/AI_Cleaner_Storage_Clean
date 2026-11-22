package com.aicleanerstorage.spaceclean.domain.model.security

/** 扫描过程中的一项：应用或文件及其风险信息。 */
data class VirusScanItemModel(
    val appName: String?,
    val packageName: String?,
    val apkPath: String?,
    val threatName: String?,
    val description: String?,
    val riskLevel: VirusRiskLevel,
) {
    /** 是否达到威胁阈值。 */
    val isThreat: Boolean get() = riskLevel == VirusRiskLevel.Threat
}
