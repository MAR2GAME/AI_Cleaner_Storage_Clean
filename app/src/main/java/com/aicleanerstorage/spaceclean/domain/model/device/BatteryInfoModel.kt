package com.aicleanerstorage.spaceclean.domain.model.device

/** 设备电量信息；字段保留 kit 语义，展示文案由 UI 解析。 */
data class BatteryInfoModel(
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
