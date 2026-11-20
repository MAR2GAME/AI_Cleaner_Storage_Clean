package com.aicleanerstorage.spaceclean.domain.model

data class DeviceCpuInfoModel(
    val hardware: String,
    val model: String,
    val cores: Int,
    val maxFrequency: String,
)
