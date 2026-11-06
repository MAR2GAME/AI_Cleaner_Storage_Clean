package com.aicleanerstorage.spaceclean.feature.home.view.home.presentation



internal object HomeReducer {
    fun reduce(state: HomeUiState, intent: HomeIntent): HomeUiState = when (intent) {
        is HomeStateChanged -> intent.value
        else -> state
    }
}
