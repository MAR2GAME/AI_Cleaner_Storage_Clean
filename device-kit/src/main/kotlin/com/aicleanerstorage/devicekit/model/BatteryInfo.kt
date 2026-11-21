package com.aicleanerstorage.devicekit.model

data class BatteryInfo(
    val levelPercent: Int,
    val health: String,
    val temperature: Float,
    val voltage: Int,
    val technology: String,
    val capacity: Int,
    val availableTime: String = "Unknown",
    val statusText: String = "Unknown",
    val isCharging: Boolean = false,
)
