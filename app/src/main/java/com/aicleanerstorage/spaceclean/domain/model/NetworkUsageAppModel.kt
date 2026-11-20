package com.aicleanerstorage.spaceclean.domain.model

data class NetworkUsageAppModel(
    val appName: String,
    val packageName: String,
    val uid: Int,
    val rxBytes: Long,
    val txBytes: Long,
) {
    val totalBytes: Long get() = rxBytes + txBytes
}
