package com.aicleanerstorage.spaceclean.domain.model

data class NetworkSpeedResultModel(
    val downloadMbps: String,
    val uploadMbps: String,
    val latencyMs: Long?,
    val measured: Boolean,
)
