package com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.onboarding.OnboardingPreferences

class SplashViewModel(private val onboardingPreferences: OnboardingPreferences) :
    MviViewModel<SplashUiState, SplashIntent, SplashEffect>(SplashUiState()) {
    override fun onIntent(intent: SplashIntent) {
        val destination = if (onboardingPreferences.isScanCompleted()) AppDestination.Main()
            else AppDestination.OnboardingScan
        val transition = reduceSplashState(currentState, intent, destination)
        updateState { transition.state }
        transition.effects.forEach(::emitEffect)
    }
}
