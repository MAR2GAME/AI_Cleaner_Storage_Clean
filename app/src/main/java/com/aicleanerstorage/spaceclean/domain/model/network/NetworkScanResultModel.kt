package com.aicleanerstorage.spaceclean.domain.model.network

/** 局域网扫描结果。 */
data class NetworkScanResultModel(
    val ssid: String?,
    val deviceIp: String?,
    val devices: List<NetworkDeviceModel>,
    val hasWifi: Boolean,
)
