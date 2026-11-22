package com.aicleanerstorage.spaceclean.domain.model.device

/** 设备硬件聚合；CPU 与传感器各自建模，避免页面拆字段。 */
data class DeviceHardwareInfoModel(
    val model: String,
    val androidVersion: String,
    val screenSize: String,
    val screenDensity: String,
    val multiTouchSupported: Boolean,
    val sensors: DeviceSensorInfoModel,
    val cpu: DeviceCpuInfoModel,
)
