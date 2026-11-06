package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedProgress
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkSpeedViewModel(
    private val repository: NetworkRepository,
    private val networkInfoReader: NetworkInfoReader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkSpeedUiState, NetworkSpeedIntent, NetworkSpeedEffect>(NetworkSpeedUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: NetworkSpeedIntent) {
        when (intent) {
            NetworkSpeedIntent.Back -> {
                if (currentState.phase != NetworkSpeedPhase.Completing) {
                    stopSpeedTest()
                    emitEffect(NetworkSpeedEffect.Exit(false))
                }
            }
            is NetworkSpeedIntent.Navigate -> emitEffect(NetworkSpeedEffect.Navigate(intent.destination))
            NetworkSpeedIntent.RefreshNetworkStateUntilNetworkAvailable -> refreshNetworkStateUntilNetworkAvailable()
            NetworkSpeedIntent.RunSpeedTest -> runSpeedTest()
            NetworkSpeedIntent.ShowResult -> showResult()
            NetworkSpeedIntent.StopSpeedTest -> stopSpeedTest()
            else -> {
        if (intent is NetworkSpeedStateChanged) {
            updateState { NetworkSpeedReducer.reduce(this, intent) }
        } else {
            Unit
        }
    
            }
        }
    }

    private fun publishState(value: NetworkSpeedUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkSpeedStateChanged(value)) }
    }

    private fun changeState(transform: (NetworkSpeedUiState) -> NetworkSpeedUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkSpeedStateChanged(transform(currentState))) }
    }

    
    private var speedTestJob: Job? = null
    private var networkRefreshJob: Job? = null


    init {
        refreshNetworkState()
    }

    fun refreshNetworkState(): Boolean {
        val hasNetwork = repository.isNetworkAvailable()
        changeState {
            it.copy(
                networkInfo = networkInfoReader.read(),
                hasNetwork = hasNetwork,
                errorMessage = null,
            )
        }
        return hasNetwork
    }

    private fun refreshNetworkStateUntilNetworkAvailable() {
        if (currentState.phase == NetworkSpeedPhase.Testing || currentState.phase == NetworkSpeedPhase.Completing) return
        networkRefreshJob?.cancel()
        networkRefreshJob =
            viewModelScope.launch(ioDispatcher) {
                repeat(NETWORK_REFRESH_RETRY_COUNT) { attempt ->
                    if (refreshNetworkState()) return@launch
                    if (attempt < NETWORK_REFRESH_RETRY_COUNT - 1) {
                        delay(NETWORK_REFRESH_RETRY_DELAY_MILLIS)
                    }
                }
            }
    }

    private fun runSpeedTest() {
        val state = currentState
        if (state.phase == NetworkSpeedPhase.Testing || state.phase == NetworkSpeedPhase.Completing || !state.hasNetwork) return

        networkRefreshJob?.cancel()
        speedTestJob?.cancel()
        trackOperationEvent(FeatureOperationEvent.OperationStarted(FeatureKey.NETWORK_SPEED, OperationAction.TEST))
        changeState {
            it.copy(
                phase = NetworkSpeedPhase.Testing,
                speed = null,
                progress = NetworkSpeedProgress(phase = "latency"),
                errorMessage = null,
            )
        }
        speedTestJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result =
                        repository.runSpeedTestWithProgress { progress ->
                            changeState { current ->
                                if (current.phase == NetworkSpeedPhase.Testing) {
                                    current.copy(progress = progress)
                                } else {
                                    current
                                }
                            }
                        }
                    changeState {
                        it.copy(
                            phase = NetworkSpeedPhase.Completing,
                            speed = result,
                            progress =
                                it.progress.copy(
                                    downloadMbps = result.downloadMbps,
                                    uploadMbps = result.uploadMbps,
                                    latencyMs = result.latencyMs,
                                    phase = "done",
                                ),
                            errorMessage = null,
                        )
                    }
                    trackOperationEvent(
                        FeatureOperationEvent.OperationFinished(
                            FeatureKey.NETWORK_SPEED,
                            OperationAction.TEST,
                            success = true,
                        ),
                    )
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (error: Throwable) {
                    trackOperationEvent(
                        FeatureOperationEvent.OperationFinished(
                            FeatureKey.NETWORK_SPEED,
                            OperationAction.TEST,
                            success = false,
                        ),
                    )
                    changeState {
                        it.copy(
                            phase = NetworkSpeedPhase.Error,
                            errorMessage = error.message,
                        )
                    }
                } finally {
                    speedTestJob = null
                }
            }
    }

    private fun showResult() {
        changeState { current ->
            if (current.phase == NetworkSpeedPhase.Completing) {
                current.copy(phase = NetworkSpeedPhase.Result)
            } else {
                current
            }
        }
    }

    private fun stopSpeedTest() {
        speedTestJob?.cancel()
        speedTestJob = null
        networkRefreshJob?.cancel()
        changeState {
            it.copy(
                phase = NetworkSpeedPhase.Idle,
                speed = null,
                progress = NetworkSpeedProgress(),
                errorMessage = null,
            )
        }
    }

    private fun trackOperationEvent(event: FeatureOperationEvent) {
        emitEffect(NetworkSpeedEffect.Operation(event))
    }

    private companion object {
        private const val NETWORK_REFRESH_RETRY_COUNT = 12
        private const val NETWORK_REFRESH_RETRY_DELAY_MILLIS = 500L
    }
}

