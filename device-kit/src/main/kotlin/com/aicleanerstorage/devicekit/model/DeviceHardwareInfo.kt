package com.aicleanerstorage.devicekit.model

data class DeviceHardwareInfo(
    val model: String,
    val androidVersion: String,
    val screenSize: String,
    val screenDensity: String,
    val multiTouchSupported: Boolean,
    val sensors: DeviceSensorInfo,
    val cpu: DeviceCpuInfo,
)
