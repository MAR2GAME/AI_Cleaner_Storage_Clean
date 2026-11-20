package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.networkkit.NetworkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkSpeedViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkSpeedUiState, NetworkSpeedAction, NetworkSpeedEvent>(NetworkSpeedUiState()) {
    override fun onAction(intent: NetworkSpeedAction) {
        when (intent) {
            NetworkSpeedAction.BackClicked -> {
                if (currentState.phase != NetworkSpeedPhase.Completing) {
                    stopSpeedTest()
                    sendEvent(NetworkSpeedEvent.Exit(false))
                }
            }
            is NetworkSpeedAction.NavigationRequested -> sendEvent(NetworkSpeedEvent.Navigate(intent.destination))
            NetworkSpeedAction.NetworkRefreshRequested -> refreshNetworkStateUntilNetworkAvailable()
            NetworkSpeedAction.SpeedTestRequested -> runSpeedTest()
            NetworkSpeedAction.ResultRequested -> showResult()
            NetworkSpeedAction.StopClicked -> stopSpeedTest()

        }
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
                networkInfo = repository.readNetworkInfo(),
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
        changeState { NetworkSpeedReducer.testStarted(it) }
        speedTestJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result =
                        repository.runSpeedTest { progress ->
                            changeState { NetworkSpeedReducer.progressUpdated(it, progress) }
                        }
                    changeState { NetworkSpeedReducer.testCompleted(it, result) }
                    dispatch(NetworkSpeedAction.ResultRequested)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (error: Throwable) {
                    changeState { NetworkSpeedReducer.testFailed(it, error.message) }
                } finally {
                    speedTestJob = null
                }
            }
    }

    private fun showResult() {
        changeState { NetworkSpeedReducer.resultRequested(it) }
    }

    private fun stopSpeedTest() {
        speedTestJob?.cancel()
        speedTestJob = null
        networkRefreshJob?.cancel()
        changeState { NetworkSpeedReducer.testStopped(it) }
    }

    private companion object {
        private const val NETWORK_REFRESH_RETRY_COUNT = 12
        private const val NETWORK_REFRESH_RETRY_DELAY_MILLIS = 500L
    }
}

