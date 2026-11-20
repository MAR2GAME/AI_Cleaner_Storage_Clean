package com.aicleanerstorage.spaceclean.data.battery.model

data class BatteryHistorySampleModel(
    val timestampMillis: Long,
    val currentMa: Float?,
    val temperatureC: Float,
)
