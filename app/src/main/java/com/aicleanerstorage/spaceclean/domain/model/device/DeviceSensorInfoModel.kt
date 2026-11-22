package com.aicleanerstorage.spaceclean.domain.model.device

/** 传感器可用性。 */
data class DeviceSensorInfoModel(
    val accelerometer: Boolean,
    val magneticField: Boolean,
    val orientation: Boolean,
    val gyroscope: Boolean,
    val light: Boolean,
    val proximity: Boolean,
    val ambientTemperature: Boolean,
)
