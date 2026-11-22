package com.aicleanerstorage.spaceclean.domain.model.battery

/** 单次电池采样：时间、电流与温度。 */
data class BatteryHistorySampleModel(
    val timestampMillis: Long,
    val currentMa: Float?,
    val temperatureC: Float,
)
