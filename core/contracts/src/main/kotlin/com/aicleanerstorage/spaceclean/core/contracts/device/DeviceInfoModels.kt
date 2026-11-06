package com.aicleanerstorage.spaceclean.core.contracts.device

data class BatteryStatusInfo(
    val statusText: String,
    val isCharging: Boolean,
)

data class DeviceHardwareInfo(
    val model: String,
    val androidVersion: String,
    val screenSize: String,
    val screenDensity: String,
    val multiTouchSupported: Boolean,
    val sensors: DeviceSensorInfo,
    val cpu: DeviceCpuInfo,
)

data class DeviceSensorInfo(
    val accelerometer: Boolean,
    val magneticField: Boolean,
    val orientation: Boolean,
    val gyroscope: Boolean,
    val light: Boolean,
    val proximity: Boolean,
    val ambientTemperature: Boolean,
)

data class DeviceCpuInfo(
    val hardware: String,
    val model: String,
    val cores: Int,
    val maxFrequency: String,
)
