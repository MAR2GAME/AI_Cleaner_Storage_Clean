package com.aicleanerstorage.spaceclean.app.view.settings.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository

class SettingsViewModel(
    private val repository: SettingsRepository,
) : MviViewModel<SettingsUiState, SettingsAction, SettingsEvent>(
        SettingsUiState(repository.readTemperatureUnit().normalizeTemperatureUnit()),
    ) {
    override fun onAction(intent: SettingsAction) {
        when (intent) {
            is SettingsAction.TemperatureUnitChanged -> {
                repository.saveTemperatureUnit(intent.unit.normalizeTemperatureUnit())
                setState { SettingsReducer.reduce(it, intent) }
            }
            is SettingsAction.NavigationRequested -> sendEvent(SettingsEvent.Navigate(intent.destination))
            SettingsAction.BackClicked -> sendEvent(SettingsEvent.NavigateBack)
            SettingsAction.TermsClicked -> sendEvent(SettingsEvent.OpenTerms)
            SettingsAction.PrivacyClicked -> sendEvent(SettingsEvent.OpenPrivacy)
            SettingsAction.RateAppClicked -> sendEvent(SettingsEvent.RateApp)
        }
    }
}

fun String.normalizeTemperatureUnit(): String = if (equals("F", ignoreCase = true)) "F" else "C"
