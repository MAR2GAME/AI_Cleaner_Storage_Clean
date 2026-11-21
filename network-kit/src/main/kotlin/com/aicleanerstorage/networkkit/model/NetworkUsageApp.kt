package com.aicleanerstorage.networkkit.model

sealed interface TrafficSource {
    data class App(val packageName: String, val appName: String) : TrafficSource
    data object SystemAndUnknown : TrafficSource
}

data class NetworkUsageApp(
    val source: TrafficSource,
    val rxBytes: Long,
    val txBytes: Long,
) {
    val totalBytes: Long get() = rxBytes + txBytes
}
