package com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation



internal object AppLockReducer {
    fun reduce(state: AppLockUiState, intent: AppLockIntent): AppLockUiState = when (intent) {
        is AppLockStateChanged -> intent.value
        else -> state
    }
}
