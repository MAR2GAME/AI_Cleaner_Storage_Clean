package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.networkkit.model.SpeedTestPhase

import com.aicleanerstorage.networkkit.model.NetworkSpeed

internal object NetworkSpeedReducer {
    fun testStarted(state: NetworkSpeedUiState): NetworkSpeedUiState =
        state.copy(
            phase = NetworkSpeedPhase.Testing,
            speed = NetworkSpeed(phase = SpeedTestPhase.Latency),
            errorMessage = null,
        )

    fun progressUpdated(state: NetworkSpeedUiState, progress: NetworkSpeed): NetworkSpeedUiState =
        if (state.phase == NetworkSpeedPhase.Testing) {
            state.copy(speed = progress)
        } else {
            state
        }

    fun testCompleted(state: NetworkSpeedUiState, result: NetworkSpeed): NetworkSpeedUiState =
        if (state.phase != NetworkSpeedPhase.Testing) state
        else state.copy(
            phase = NetworkSpeedPhase.Completing,
            speed = result,
            errorMessage = null,
        )

    fun testFailed(state: NetworkSpeedUiState, message: String?): NetworkSpeedUiState =
        if (state.phase != NetworkSpeedPhase.Testing) state
        else state.copy(
            phase = NetworkSpeedPhase.Error,
            errorMessage = message,
        )

    fun resultRequested(state: NetworkSpeedUiState): NetworkSpeedUiState =
        if (state.phase == NetworkSpeedPhase.Completing) {
            state.copy(phase = NetworkSpeedPhase.Result)
        } else {
            state
        }

    fun testStopped(state: NetworkSpeedUiState): NetworkSpeedUiState =
        state.copy(
            phase = NetworkSpeedPhase.Idle,
            speed = NetworkSpeed(),
            errorMessage = null,
        )
}
