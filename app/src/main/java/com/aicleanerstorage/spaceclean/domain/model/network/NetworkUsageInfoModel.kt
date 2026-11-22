package com.aicleanerstorage.spaceclean.domain.model.network

/** 流量汇总；只表达某个时间窗口内的流量实体。 */
data class NetworkUsageInfoModel(
    val wifiRxBytes: Long,
    val wifiTxBytes: Long,
    val cellularRxBytes: Long,
    val cellularTxBytes: Long,
    val wifiApps: List<NetworkUsageAppModel> = emptyList(),
    val cellularApps: List<NetworkUsageAppModel> = emptyList(),
) {
    val wifiTotalBytes: Long get() = wifiRxBytes + wifiTxBytes

    val cellularTotalBytes: Long get() = cellularRxBytes + cellularTxBytes
}
