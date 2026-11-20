package com.aicleanerstorage.spaceclean.domain.model

data class NetworkSpeedProgressModel(
    val downloadMbps: String? = null,
    val uploadMbps: String? = null,
    val latencyMs: Long? = null,
    val phase: String = "idle",
)
