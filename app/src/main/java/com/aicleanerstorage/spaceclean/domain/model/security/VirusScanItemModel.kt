package com.aicleanerstorage.spaceclean.domain.model.security

/** 扫描过程中的一项：应用或文件及其风险信息。 */
data class VirusScanItemModel(
    val packageName: String?,
    val apkPath: String?,
    val appName: String?,
    val virusName: String?,
    val summary: List<String>?,
    val score: Int,
)
