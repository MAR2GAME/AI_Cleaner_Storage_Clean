package com.aicleanerstorage.spaceclean.domain.model


data class VirusScanItemModel(
    val packageName: String?,
    val apkPath: String?,
    val appName: String?,
    val virusName: String?,
    val summary: List<String>?,
    val score: Int,
)
