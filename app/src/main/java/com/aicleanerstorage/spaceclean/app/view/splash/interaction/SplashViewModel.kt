package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository

class SplashViewModel(
    private val settingsRepository: SettingsRepository,
) : MviViewModel<SplashUiState, SplashAction, SplashEvent>(SplashUiState()) {
    override fun onAction(intent: SplashAction) {
        val destination =
            if (settingsRepository.isOnboardingScanCompleted()) {
                AppDestination.Main()
            } else {
                AppDestination.OnboardingScan
            }
        val transition = SplashReducer.reduce(currentState, intent, destination)
        setState { transition.state }
        transition.events.forEach(::sendEvent)
    }
}
