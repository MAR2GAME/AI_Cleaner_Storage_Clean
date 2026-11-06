package com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.BatteryCurrentRange
import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.DeviceInfoUiState

sealed interface BatteryInfoIntent {
    data object Resumed : BatteryInfoIntent
    data object Paused : BatteryInfoIntent
    data object Back : BatteryInfoIntent
    data object QuitScan : BatteryInfoIntent
    data object ResumeScan : BatteryInfoIntent
    data class RangeSelected(val range: BatteryCurrentRange) : BatteryInfoIntent
}

sealed interface BatteryInfoEffect {
    data object Exit : BatteryInfoEffect
}

internal data class BatteryInfoStateChanged(val value: DeviceInfoUiState) : BatteryInfoIntent

