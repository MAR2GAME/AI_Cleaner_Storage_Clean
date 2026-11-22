package com.aicleanerstorage.spaceclean.domain.model.network

/** 流量归属。 */
sealed interface TrafficSource {
    data class App(val packageName: String, val appName: String) : TrafficSource

    data object SystemAndUnknown : TrafficSource
}
