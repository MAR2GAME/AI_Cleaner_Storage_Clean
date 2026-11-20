package com.aicleanerstorage.networkkit.model

enum class SpeedTestPhase { Idle, Latency, Download, Upload, Done }

data class NetworkSpeed(
    val downloadMbps: Double? = null,
    val uploadMbps: Double? = null,
    val latencyMs: Long? = null,
    val phase: SpeedTestPhase = SpeedTestPhase.Idle,
    val measured: Boolean = false,
)

internal fun measuredNetworkSpeed(download: Double?, upload: Double?, latency: Long?): NetworkSpeed? =
    if (download == null && upload == null) null else NetworkSpeed(download, upload, latency, phase = SpeedTestPhase.Done, measured = true)
