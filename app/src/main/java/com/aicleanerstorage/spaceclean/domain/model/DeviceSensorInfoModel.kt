package com.aicleanerstorage.spaceclean.domain.model

data class DeviceSensorInfoModel(
    val accelerometer: Boolean,
    val magneticField: Boolean,
    val orientation: Boolean,
    val gyroscope: Boolean,
    val light: Boolean,
    val proximity: Boolean,
    val ambientTemperature: Boolean,
)
