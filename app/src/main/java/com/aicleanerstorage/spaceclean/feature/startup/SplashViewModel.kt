package com.aicleanerstorage.spaceclean.feature.startup

import androidx.lifecycle.ViewModel
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SplashViewModel(
    private val onboardingPreferences: OnboardingPreferences,
) : ViewModel() {
    private var machineState = SplashMachineState()
    private val _uiState = MutableStateFlow(machineState.toUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val effectsChannel = Channel<SplashEffect>(Channel.BUFFERED)
    val effects = effectsChannel.receiveAsFlow()

    fun onAction(action: SplashAction) {
        val transition =
            reduceSplashState(
                state = machineState,
                action = action,
                normalDestination = startupDestination(),
            )
        if (transition.state != machineState) {
            machineState = transition.state
            _uiState.value = machineState.toUiState()
        }
        transition.effects.forEach { effect -> effectsChannel.trySend(effect) }
    }

    private fun startupDestination(): AppDestination =
        if (onboardingPreferences.isScanCompleted()) AppDestination.Home else AppDestination.OnboardingScan
}
