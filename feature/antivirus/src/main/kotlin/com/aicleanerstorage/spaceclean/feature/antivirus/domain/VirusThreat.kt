package com.aicleanerstorage.spaceclean.feature.antivirus.domain

data class VirusThreat(
    val id: String,
    val packageName: String?,
    val apkPath: String?,
    val title: String,
    val description: String,
    val isFile: Boolean,
    val icon: String?
)

