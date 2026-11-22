package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel

@Composable
internal fun SensorsCard(hardware: DeviceHardwareInfoModel) {
    InfoSection(
        title = stringResource(R.string.device_sensors),
        rows =
            listOf(
                stringResource(R.string.device_accelerometer_sensor) to supportText(hardware.sensors.accelerometer),
                stringResource(R.string.device_magnetic_field_sensor) to supportText(hardware.sensors.magneticField),
                stringResource(R.string.device_orientation_sensor) to supportText(hardware.sensors.orientation),
                stringResource(R.string.device_gyroscope_sensor) to supportText(hardware.sensors.gyroscope),
                stringResource(R.string.device_light_sensor) to supportText(hardware.sensors.light),
                stringResource(R.string.device_distance_sensor) to supportText(hardware.sensors.proximity),
                stringResource(R.string.device_temperature_sensor) to supportText(hardware.sensors.ambientTemperature),
            ),
    )
}
