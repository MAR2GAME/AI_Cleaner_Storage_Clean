package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryHealth
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryStatus
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.currentBatteryCapacityMah
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.formatBatteryCapacityMah
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.formatBatteryTemperature
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.formatBatteryVoltage
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.orPlaceholder

@Composable
internal fun BatteryCard(uiState: DeviceInfoUiState) {
    InfoSection(
        title = stringResource(R.string.device_battery),
        rows =
            listOf(
                stringResource(R.string.battery_health_status) to localizedBatteryHealth(uiState.battery.health),
                stringResource(R.string.battery_current_capacity) to
                    formatBatteryCapacityMah(currentBatteryCapacityMah(uiState.battery)),
                stringResource(R.string.battery_total_capacity) to formatBatteryCapacityMah(uiState.battery.capacity),
                stringResource(R.string.battery_voltage) to formatBatteryVoltage(uiState.battery.voltage),
                stringResource(R.string.battery_temperature) to
                    formatBatteryTemperature(uiState.battery.temperature, uiState.temperatureUnit),
                stringResource(R.string.battery_status) to localizedBatteryStatus(uiState.battery.status),
                stringResource(R.string.battery_charging_status) to
                    if (uiState.battery.isCharging) {
                        stringResource(R.string.battery_charging)
                    } else {
                        stringResource(R.string.battery_not_charging)
                    },
                stringResource(R.string.battery_technology) to uiState.battery.technology.orPlaceholder(),
            ),
    )
}

@Composable
private fun localizedBatteryHealth(health: BatteryHealth): String =
    when (health) {
        BatteryHealth.Good -> stringResource(R.string.battery_health_good)
        BatteryHealth.Cold -> stringResource(R.string.battery_health_cold)
        BatteryHealth.Dead -> stringResource(R.string.battery_health_dead)
        BatteryHealth.Overheat -> stringResource(R.string.battery_health_overheat)
        BatteryHealth.Overvoltage -> stringResource(R.string.battery_health_overvoltage)
        BatteryHealth.Failure -> stringResource(R.string.battery_health_failure)
        BatteryHealth.Unknown -> stringResource(R.string.device_unknown)
    }

@Composable
private fun localizedBatteryStatus(status: BatteryStatus): String =
    when (status) {
        BatteryStatus.Charging -> stringResource(R.string.battery_charging)
        BatteryStatus.Discharging -> stringResource(R.string.battery_discharging)
        BatteryStatus.Full -> stringResource(R.string.battery_full)
        BatteryStatus.NotCharging -> stringResource(R.string.battery_not_charging)
        BatteryStatus.Unknown -> stringResource(R.string.device_unknown)
    }
