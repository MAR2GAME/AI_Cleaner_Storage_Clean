package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase

internal object NetworkSpeedReducer {
    fun testStarted(state: NetworkSpeedUiState): NetworkSpeedUiState =
        state.copy(
            phase = NetworkSpeedPhase.Testing,
            speed = NetworkSpeedModel(phase = SpeedTestPhase.Latency),
        )

    fun progressUpdated(
        state: NetworkSpeedUiState,
        progress: NetworkSpeedModel,
    ): NetworkSpeedUiState =
        if (state.phase == NetworkSpeedPhase.Testing) {
            state.copy(speed = progress)
        } else {
            state
        }

    fun testCompleted(
        state: NetworkSpeedUiState,
        result: NetworkSpeedModel,
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
            speed = NetworkSpeedModel(),
        )
}
