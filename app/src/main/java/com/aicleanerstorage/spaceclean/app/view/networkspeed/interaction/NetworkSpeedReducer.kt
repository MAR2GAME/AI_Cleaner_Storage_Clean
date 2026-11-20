package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedProgressModel

internal object NetworkSpeedReducer {
    fun testStarted(state: NetworkSpeedUiState): NetworkSpeedUiState =
        state.copy(
            phase = NetworkSpeedPhase.Testing,
            speed = null,
            progress = NetworkSpeedProgressModel(phase = "latency"),
            errorMessage = null,
        )

    fun progressUpdated(state: NetworkSpeedUiState, progress: NetworkSpeedProgressModel): NetworkSpeedUiState =
        if (state.phase == NetworkSpeedPhase.Testing) {
            state.copy(progress = progress)
        } else {
            state
        }

    fun testCompleted(state: NetworkSpeedUiState, result: NetworkSpeedResultModel): NetworkSpeedUiState =
        if (state.phase != NetworkSpeedPhase.Testing) state
        else state.copy(
            phase = NetworkSpeedPhase.Completing,
            speed = result,
            progress =
                state.progress.copy(
                    downloadMbps = result.downloadMbps,
                    uploadMbps = result.uploadMbps,
                    latencyMs = result.latencyMs,
                    phase = "done",
                ),
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
            speed = null,
            progress = NetworkSpeedProgressModel(),
            errorMessage = null,
        )
}
