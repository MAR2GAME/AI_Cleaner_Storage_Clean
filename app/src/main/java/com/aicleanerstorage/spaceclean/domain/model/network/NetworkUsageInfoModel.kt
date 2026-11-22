package com.aicleanerstorage.spaceclean.domain.model.network

/** 流量汇总。 */
data class NetworkUsageInfoModel(
    val wifiRxBytes: Long,
    val wifiTxBytes: Long,
    val cellularRxBytes: Long,
    val cellularTxBytes: Long,
    val wifiApps: List<NetworkUsageAppModel> = emptyList(),
    val cellularApps: List<NetworkUsageAppModel> = emptyList(),
    val fallbackApps: List<NetworkUsageAppModel> = emptyList(),
    val isToday: Boolean,
    val needsUsageAccess: Boolean,
) {
    val wifiTotalBytes: Long get() = wifiRxBytes + wifiTxBytes

    val cellularTotalBytes: Long get() = cellularRxBytes + cellularTxBytes
}
