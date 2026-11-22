package com.aicleanerstorage.spaceclean.domain.model.network

/** 一次测速的结果；[measured] 为 false 时字段可能全为 null。 */
data class NetworkSpeedModel(
    val downloadMbps: Double? = null,
    val uploadMbps: Double? = null,
    val latencyMs: Long? = null,
    val phase: SpeedTestPhase = SpeedTestPhase.Idle,
    val measured: Boolean = false,
)
