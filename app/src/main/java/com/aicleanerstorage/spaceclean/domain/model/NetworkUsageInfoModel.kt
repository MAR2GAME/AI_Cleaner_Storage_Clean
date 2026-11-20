package com.aicleanerstorage.spaceclean.domain.model

const val UNKNOWN_NETWORK_TRAFFIC_PACKAGE = "com.aicleanerstorage.spaceclean.unknown_network_traffic"

data class NetworkUsageInfoModel(
    val wifiRxBytes: Long,
    val wifiTxBytes: Long,
    val cellularRxBytes: Long,
    val cellularTxBytes: Long,
    val apps: List<NetworkUsageAppModel> = emptyList(),
    val wifiApps: List<NetworkUsageAppModel> = apps,
    val cellularApps: List<NetworkUsageAppModel> = emptyList(),
    val fallbackApps: List<NetworkUsageAppModel> = emptyList(),
    val isToday: Boolean,
    val needsUsageAccess: Boolean,
) {
    val wifiTotalBytes: Long get() = wifiRxBytes + wifiTxBytes
    val cellularTotalBytes: Long get() = cellularRxBytes + cellularTxBytes
}
