package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.networkkit.model.NetworkSpeed
import com.aicleanerstorage.networkkit.model.SpeedTestPhase

internal object NetworkSpeedReducer {
    fun testStarted(state: NetworkSpeedUiState): NetworkSpeedUiState =
        state.copy(
            phase = NetworkSpeedPhase.Testing,
            speed = NetworkSpeed(phase = SpeedTestPhase.Latency),
        )

    fun progressUpdated(
        state: NetworkSpeedUiState,
        progress: NetworkSpeed,
    ): NetworkSpeedUiState =
        if (state.phase == NetworkSpeedPhase.Testing) {
            state.copy(speed = progress)
        } else {
            state
        }

    fun testCompleted(
        state: NetworkSpeedUiState,
        result: NetworkSpeed,
    ): NetworkSpeedUiState =
        if (state.phase != NetworkSpeedPhase.Testing) {
            state
        } else {
            state.copy(
                phase = NetworkSpeedPhase.Completing,
                speed = result,
            )
        }

    fun testFailed(state: NetworkSpeedUiState): NetworkSpeedUiState =
        if (state.phase != NetworkSpeedPhase.Testing) {
            state
        } else {
            state.copy(
                phase = NetworkSpeedPhase.Error,
            )
        }

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
        )
}
