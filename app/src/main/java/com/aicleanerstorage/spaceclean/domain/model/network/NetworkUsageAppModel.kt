package com.aicleanerstorage.spaceclean.domain.model.network

/** 单个 App / 系统来源的流量。 */
data class NetworkUsageAppModel(
    val source: TrafficSource,
    val rxBytes: Long,
    val txBytes: Long,
) {
    val totalBytes: Long get() = rxBytes + txBytes
}
