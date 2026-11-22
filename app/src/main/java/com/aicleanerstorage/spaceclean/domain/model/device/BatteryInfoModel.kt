package com.aicleanerstorage.spaceclean.domain.model.device

/** 设备电量信息；状态用枚举表达，展示文案由 UI 解析。 */
data class BatteryInfoModel(
    val levelPercent: Int,
    val health: BatteryHealth = BatteryHealth.Unknown,
    val temperature: Float = 0f,
    val voltage: Int = -1,
    val technology: String = "Unknown",
    val capacity: Int = 0,
    val availableTimeMinutes: Int? = null,
    val status: BatteryStatus = BatteryStatus.Unknown,
    val isCharging: Boolean = false,
)
