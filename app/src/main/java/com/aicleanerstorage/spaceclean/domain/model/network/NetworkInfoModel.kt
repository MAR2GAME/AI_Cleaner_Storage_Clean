package com.aicleanerstorage.spaceclean.domain.model.network

/** 当前网络；ssid / ip 在无权限或飞行模式时为 null。 */
data class NetworkInfoModel(
    val transport: NetworkTransport = NetworkTransport.Unknown,
    val ssid: String? = null,
    val ip: String? = null,
)
