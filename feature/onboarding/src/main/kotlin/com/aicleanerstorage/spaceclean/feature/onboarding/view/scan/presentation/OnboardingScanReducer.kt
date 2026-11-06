package com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation



internal object OnboardingReducer {
    fun reduce(state: OnboardingUiState, intent: OnboardingIntent): OnboardingUiState = when (intent) {
        is OnboardingLoaded -> intent.value.copy(completedStep = state.completedStep)
        is OnboardingProgress -> state.copy(completedStep = intent.step)
        else -> state
    }
}
