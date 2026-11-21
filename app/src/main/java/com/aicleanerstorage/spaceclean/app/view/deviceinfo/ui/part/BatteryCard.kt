package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.currentBatteryCapacityMah
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.formatBatteryCapacityMah
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.formatBatteryTemperature
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.formatBatteryVoltage
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.orPlaceholder

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
                stringResource(R.string.battery_status) to localizedBatteryStatus(uiState.battery.statusText),
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
private fun localizedBatteryHealth(health: String): String =
    when (health) {
        "Good" -> stringResource(R.string.battery_health_good)
        "Cold" -> stringResource(R.string.battery_health_cold)
        "Dead" -> stringResource(R.string.battery_health_dead)
        "Overheat" -> stringResource(R.string.battery_health_overheat)
        "Overvoltage" -> stringResource(R.string.battery_health_overvoltage)
        "Failure" -> stringResource(R.string.battery_health_failure)
        else -> stringResource(R.string.device_unknown)
    }

@Composable
private fun localizedBatteryStatus(status: String): String =
    when (status) {
        "Charging" -> stringResource(R.string.battery_charging)
        "Discharging" -> stringResource(R.string.battery_discharging)
        "Full" -> stringResource(R.string.battery_full)
        "Not Charging" -> stringResource(R.string.battery_not_charging)
        else -> stringResource(R.string.device_unknown)
    }
