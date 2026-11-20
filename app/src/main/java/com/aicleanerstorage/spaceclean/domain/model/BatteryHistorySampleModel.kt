package com.aicleanerstorage.spaceclean.domain.model

data class BatteryHistorySampleModel(
    val timestampMillis: Long,
    val currentMa: Float?,
    val temperatureC: Float,
)
