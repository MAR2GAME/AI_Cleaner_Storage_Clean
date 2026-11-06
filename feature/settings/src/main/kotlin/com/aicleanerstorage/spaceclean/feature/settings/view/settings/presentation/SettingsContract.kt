package com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

data class SettingsUiState(val temperatureUnit: String)

sealed interface SettingsIntent {
    data class TemperatureUnitChanged(val unit: String) : SettingsIntent
    data class Navigate(val destination: AppDestination) : SettingsIntent
    data object Back : SettingsIntent
    data object OpenTerms : SettingsIntent
    data object OpenPrivacy : SettingsIntent
    data object RateApp : SettingsIntent
}

sealed interface SettingsEffect {
    data class Navigate(val destination: AppDestination) : SettingsEffect
    data object Back : SettingsEffect
    data object OpenTerms : SettingsEffect
    data object OpenPrivacy : SettingsEffect
    data object RateApp : SettingsEffect
}
