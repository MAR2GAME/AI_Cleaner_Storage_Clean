package com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared

data class NetworkInfo(
    val type: String = "--",
    val ssid: String = "--",
    val ip: String = "--",
)

fun interface NetworkInfoReader {
    fun read(): NetworkInfo
}

