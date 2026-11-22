package com.aicleanerstorage.spaceclean.domain.model.network

/** 局域网设备。 */
data class NetworkDeviceModel(
    val ip: String,
    val hostName: String?,
    val macAddress: String?,
)
