package com.aicleanerstorage.spaceclean.app.view.onboarding.interaction



internal object OnboardingReducer {
    fun reduce(state: OnboardingUiState, intent: OnboardingAction): OnboardingUiState = when (intent) {
        is OnboardingLoaded -> intent.value.copy(completedStep = state.completedStep)
        is OnboardingProgressUpdated -> state.copy(completedStep = intent.step)
        else -> state
    }
}
