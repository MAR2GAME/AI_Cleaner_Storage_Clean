package com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation

import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel

class SettingsViewModel(private val repository: SettingsRepository) :
    MviViewModel<SettingsUiState, SettingsIntent, SettingsEffect>(
        SettingsUiState(repository.readTemperatureUnit().normalizeTemperatureUnit()),
    ) {
    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.TemperatureUnitChanged -> {
                repository.saveTemperatureUnit(intent.unit.normalizeTemperatureUnit())
                updateState { SettingsReducer.reduce(this, intent) }
            }
            is SettingsIntent.Navigate -> emitEffect(SettingsEffect.Navigate(intent.destination))
            SettingsIntent.Back -> emitEffect(SettingsEffect.Back)
            SettingsIntent.OpenTerms -> emitEffect(SettingsEffect.OpenTerms)
            SettingsIntent.OpenPrivacy -> emitEffect(SettingsEffect.OpenPrivacy)
            SettingsIntent.RateApp -> emitEffect(SettingsEffect.RateApp)
        }
    }
}

fun String.normalizeTemperatureUnit(): String = if (equals("F", ignoreCase = true)) "F" else "C"
