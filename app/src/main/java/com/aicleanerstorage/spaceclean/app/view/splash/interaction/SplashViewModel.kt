package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository

class SplashViewModel(
    private val settingsRepository: AppSettingsRepository,
) : MviViewModel<SplashUiState, SplashAction, SplashEvent>(SplashUiState()) {
    override fun onAction(intent: SplashAction) {
        val destination =
            if (
                settingsRepository.isFirstOnboardingPending() || settingsRepository.isSecondOnboardingPending()
            ) {
                AppDestination.OnboardingScan
            } else {
                AppDestination.Main()
            }
        val transition = SplashReducer.reduce(currentState, intent, destination)
        setState { transition.state }
        transition.events.forEach(::sendEvent)
    }
}
