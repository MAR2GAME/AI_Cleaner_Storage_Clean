package com.aicleanerstorage.spaceclean.domain.model

data class NetworkScanResultModel(
    val ssid: String,
    val gatewayIp: String,
    val dnsIp: String,
    val deviceIp: String,
    val devices: List<NetworkDeviceInfoModel>,
    val hasWifi: Boolean,
)
