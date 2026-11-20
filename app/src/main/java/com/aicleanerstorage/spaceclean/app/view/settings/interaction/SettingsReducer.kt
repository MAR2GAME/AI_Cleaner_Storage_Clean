package com.aicleanerstorage.spaceclean.app.view.settings.interaction



object SettingsReducer {
    fun reduce(state: SettingsUiState, intent: SettingsAction): SettingsUiState = when (intent) {
        is SettingsAction.TemperatureUnitChanged -> state.copy(temperatureUnit = intent.unit.normalizeTemperatureUnit())
        else -> state
    }
}
