package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkSpeedReducerTest {
    @Test
    fun `test started enters testing and resets progress`() {
        val state =
            NetworkSpeedReducer.testStarted(
                NetworkSpeedUiState(
                    phase = NetworkSpeedPhase.Result,
                    speed = NetworkSpeedModel(downloadMbps = 9.0),
                ),
            )

        assertEquals(NetworkSpeedPhase.Testing, state.phase)
        assertEquals(SpeedTestPhase.Latency, state.speed.phase)
        assertEquals(null, state.speed.downloadMbps)
        assertEquals(null, state.error)
    }

    @Test
    fun `progress only applies while testing`() {
        val testing = NetworkSpeedUiState(phase = NetworkSpeedPhase.Testing)
        assertEquals(
            12.0,
            NetworkSpeedReducer.progressUpdated(testing, NetworkSpeedModel(downloadMbps = 12.0)).speed.downloadMbps,
        )

        val idle = NetworkSpeedUiState(phase = NetworkSpeedPhase.Idle)
        assertEquals(idle, NetworkSpeedReducer.progressUpdated(idle, NetworkSpeedModel(downloadMbps = 12.0)))
    }

    @Test
    fun `test completed only moves from testing to completing`() {
        val result = NetworkSpeedModel(downloadMbps = 10.0, uploadMbps = 5.0)
        val completed =
            NetworkSpeedReducer.testCompleted(NetworkSpeedUiState(phase = NetworkSpeedPhase.Testing), result)
        assertEquals(NetworkSpeedPhase.Completing, completed.phase)
        assertEquals(result, completed.speed)

        val idle = NetworkSpeedUiState(phase = NetworkSpeedPhase.Idle)
        assertEquals(idle, NetworkSpeedReducer.testCompleted(idle, result))
    }

    @Test
    fun `failure maps to the speed test error`() {
        val failed = NetworkSpeedReducer.testFailed(NetworkSpeedUiState(phase = NetworkSpeedPhase.Testing))

        assertEquals(NetworkSpeedPhase.Error, failed.phase)
        assertEquals(UiText.Res(R.string.speed_test_failed), failed.error)
    }

    @Test
    fun `result only moves from completing to result`() {
        assertEquals(
            NetworkSpeedPhase.Result,
            NetworkSpeedReducer.resultRequested(NetworkSpeedUiState(phase = NetworkSpeedPhase.Completing)).phase,
        )

        val idle = NetworkSpeedUiState(phase = NetworkSpeedPhase.Idle)
        assertEquals(idle, NetworkSpeedReducer.resultRequested(idle))
    }

    @Test
    fun `stop resets to idle and clears the measurement`() {
        val stopped =
            NetworkSpeedReducer.testStopped(
                NetworkSpeedUiState(
                    phase = NetworkSpeedPhase.Testing,
                    speed = NetworkSpeedModel(downloadMbps = 10.0),
                ),
            )

        assertEquals(NetworkSpeedPhase.Idle, stopped.phase)
        assertEquals(NetworkSpeedModel(), stopped.speed)
        assertEquals(null, stopped.error)
    }
}
