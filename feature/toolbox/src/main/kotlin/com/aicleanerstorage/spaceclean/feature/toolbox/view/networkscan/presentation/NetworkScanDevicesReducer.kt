package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation



internal object NetworkScanDevicesReducer {
    fun reduce(state: NetworkScanDevicesUiState, intent: NetworkScanDevicesIntent): NetworkScanDevicesUiState = when (intent) {
        is NetworkScanDevicesStateChanged -> intent.value
        else -> state
    }
}

