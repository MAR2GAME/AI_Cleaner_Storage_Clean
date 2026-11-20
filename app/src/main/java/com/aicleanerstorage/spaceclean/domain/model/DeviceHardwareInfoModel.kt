package com.aicleanerstorage.spaceclean.domain.model

data class DeviceHardwareInfoModel(
    val model: String,
    val androidVersion: String,
    val screenSize: String,
    val screenDensity: String,
    val multiTouchSupported: Boolean,
    val sensors: DeviceSensorInfoModel,
    val cpu: DeviceCpuInfoModel,
)
