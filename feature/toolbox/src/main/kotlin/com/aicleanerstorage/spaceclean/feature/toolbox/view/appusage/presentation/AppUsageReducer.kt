package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation



internal object AppUsageReducer {
    fun reduce(state: AppUsageUiState, intent: AppUsageIntent): AppUsageUiState = when (intent) {
        is AppUsageStateChanged -> intent.value
        else -> state
    }
}

