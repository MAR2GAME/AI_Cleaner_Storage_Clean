package com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery

data class BatteryHistorySample(
    val timestampMillis: Long,
    val currentMa: Float?,
    val temperatureC: Float,
)

