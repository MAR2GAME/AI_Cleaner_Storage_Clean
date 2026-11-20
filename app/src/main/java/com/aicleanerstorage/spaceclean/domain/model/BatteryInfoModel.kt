package com.aicleanerstorage.spaceclean.domain.model

data class BatteryInfoModel(
    val levelPercent: Int,
    val health: String,
    val temperature: Float,
    val voltage: Int,
    val technology: String,
    val capacity: Int,
    val availableTime: String = "Unknown",
)
