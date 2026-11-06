package com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.*



internal object DeviceInfoReducer {
    fun reduce(state: DeviceInfoUiState, intent: DeviceInfoIntent): DeviceInfoUiState = when (intent) {
        is DeviceInfoStateChanged -> intent.value
        else -> state
    }
}

