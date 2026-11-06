package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation



internal object NetworkUsageReducer {
    fun reduce(state: NetworkUsageUiState, intent: NetworkUsageIntent): NetworkUsageUiState = when (intent) {
        is NetworkUsageStateChanged -> intent.value
        else -> state
    }
}

