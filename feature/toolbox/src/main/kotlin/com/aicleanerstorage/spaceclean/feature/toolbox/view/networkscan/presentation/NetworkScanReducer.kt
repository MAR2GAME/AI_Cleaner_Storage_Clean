package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation



internal object NetworkScanReducer {
    fun reduce(state: NetworkScanUiState, intent: NetworkScanIntent): NetworkScanUiState = when (intent) {
        is NetworkScanStateChanged -> intent.value
        else -> state
    }
}

