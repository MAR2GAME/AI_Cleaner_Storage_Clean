package com.aicleanerstorage.networkkit.model

enum class NetworkTransport { Wifi, Cellular, Ethernet, Unknown }

data class NetworkInfo(
    val transport: NetworkTransport = NetworkTransport.Unknown,
    val ssid: String? = null,
    val ip: String? = null,
)
