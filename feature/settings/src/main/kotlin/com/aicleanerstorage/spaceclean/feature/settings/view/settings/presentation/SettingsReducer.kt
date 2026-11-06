package com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation



object SettingsReducer {
    fun reduce(state: SettingsUiState, intent: SettingsIntent): SettingsUiState = when (intent) {
        is SettingsIntent.TemperatureUnitChanged -> state.copy(temperatureUnit = intent.unit.normalizeTemperatureUnit())
        else -> state
    }
}
