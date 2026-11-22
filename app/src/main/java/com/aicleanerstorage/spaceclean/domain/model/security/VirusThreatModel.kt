package com.aicleanerstorage.spaceclean.domain.model.security

/** 面向结果页的风险项展示模型。 */
data class VirusThreatModel(
    val id: String,
    val packageName: String?,
    val apkPath: String?,
    val title: String,
    val description: String,
    val isFile: Boolean,
    val icon: String?,
)
