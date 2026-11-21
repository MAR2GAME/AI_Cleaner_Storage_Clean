package com.aicleanerstorage.networkkit.model

data class NetworkScanResult(
    val ssid: String?,
    val gatewayIp: String?,
    val dnsIp: String?,
    val deviceIp: String?,
    val devices: List<NetworkDeviceInfo>,
    val hasWifi: Boolean,
)
