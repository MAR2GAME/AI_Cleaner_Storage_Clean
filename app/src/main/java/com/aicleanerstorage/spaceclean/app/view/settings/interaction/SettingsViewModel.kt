package com.aicleanerstorage.spaceclean.app.view.settings.interaction

import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.integration.mar2VersionLabel
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository

class SettingsViewModel(
    private val repository: AppSettingsRepository,
) : MviViewModel<SettingsUiState, SettingsAction, SettingsEvent>(
        SettingsUiState(
            temperatureUnit = repository.readTemperatureUnit().normalizeTemperatureUnit(),
            versionLabel = mar2VersionLabel(BuildConfig.VERSION_NAME),
        ),
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
            is SettingsAction.RateDialogClosed -> sendEvent(SettingsEvent.TrackRatePopup(intent.ifOk))
            SettingsAction.Resumed -> setState { it.copy(versionLabel = mar2VersionLabel(BuildConfig.VERSION_NAME)) }
        }
    }
}

fun String.normalizeTemperatureUnit(): String = if (equals("F", ignoreCase = true)) "F" else "C"
