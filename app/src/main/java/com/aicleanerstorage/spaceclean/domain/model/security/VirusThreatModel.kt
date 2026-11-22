package com.aicleanerstorage.spaceclean.domain.model.security

data class VirusThreatModel(
    val id: String,
    val packageName: String?,
    val apkPath: String?,
    val title: String,
    val description: String,
    val isFile: Boolean,
    val icon: String?,
)
