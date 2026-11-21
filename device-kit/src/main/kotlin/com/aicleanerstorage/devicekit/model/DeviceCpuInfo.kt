package com.aicleanerstorage.devicekit.model

data class DeviceCpuInfo(
    val hardware: String,
    val model: String,
    val cores: Int,
    val maxFrequency: String,
)
