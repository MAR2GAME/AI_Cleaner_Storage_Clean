package com.aicleanerstorage.spaceclean.app.view.settings.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

data class SettingsUiState(
    val temperatureUnit: String,
)

sealed interface SettingsAction {
    data class TemperatureUnitChanged(
        val unit: String,
    ) : SettingsAction

    data class NavigationRequested(
        val destination: AppDestination,
    ) : SettingsAction

    data object BackClicked : SettingsAction

    data object TermsClicked : SettingsAction

    data object PrivacyClicked : SettingsAction

    data object RateAppClicked : SettingsAction

    data class RateDialogClosed(
        val ifOk: Boolean,
    ) : SettingsAction
}

sealed interface SettingsEvent {
    data class Navigate(
        val destination: AppDestination,
    ) : SettingsEvent

    data object NavigateBack : SettingsEvent

    data object OpenTerms : SettingsEvent

    data object OpenPrivacy : SettingsEvent

    data object RateApp : SettingsEvent

    data class TrackRatePopup(
        val ifOk: Boolean,
    ) : SettingsEvent
}
