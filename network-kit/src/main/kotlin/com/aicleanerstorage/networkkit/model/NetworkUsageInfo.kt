package com.aicleanerstorage.networkkit.model

data class NetworkUsageInfo(
    val wifiRxBytes: Long,
    val wifiTxBytes: Long,
    val cellularRxBytes: Long,
    val cellularTxBytes: Long,
    val wifiApps: List<NetworkUsageApp> = emptyList(),
    val cellularApps: List<NetworkUsageApp> = emptyList(),
    val fallbackApps: List<NetworkUsageApp> = emptyList(),
    val isToday: Boolean,
    val needsUsageAccess: Boolean,
) {
    val wifiTotalBytes: Long get() = wifiRxBytes + wifiTxBytes
    val cellularTotalBytes: Long get() = cellularRxBytes + cellularTxBytes
}
