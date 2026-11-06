package com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.DeviceInfoUiState

internal object BatteryInfoReducer {
    fun reduce(state: DeviceInfoUiState, intent: BatteryInfoIntent): DeviceInfoUiState = when (intent) {
        is BatteryInfoStateChanged -> intent.value
        else -> state
    }
}

