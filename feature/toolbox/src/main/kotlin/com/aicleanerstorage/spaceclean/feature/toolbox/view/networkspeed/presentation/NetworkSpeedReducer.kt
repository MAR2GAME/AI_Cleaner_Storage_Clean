package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation



internal object NetworkSpeedReducer {
    fun reduce(state: NetworkSpeedUiState, intent: NetworkSpeedIntent): NetworkSpeedUiState = when (intent) {
        is NetworkSpeedStateChanged -> intent.value
        else -> state
    }
}

