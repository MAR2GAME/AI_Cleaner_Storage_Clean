package com.aicleanerstorage.spaceclean.domain.model.device

/** 系统电池健康状态。 */
enum class BatteryHealth {
    Unknown,
    Good,
    Cold,
    Dead,
    Overheat,
    Overvoltage,
    Failure,
}
