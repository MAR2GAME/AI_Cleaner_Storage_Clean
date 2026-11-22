package com.aicleanerstorage.spaceclean.domain.model.battery

data class BatteryHistorySampleModel(
    val timestampMillis: Long,
    val currentMa: Float?,
    val temperatureC: Float,
)
