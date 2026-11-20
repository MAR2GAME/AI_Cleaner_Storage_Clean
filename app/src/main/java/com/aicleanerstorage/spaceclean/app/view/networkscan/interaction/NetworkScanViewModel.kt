package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.networkkit.NetworkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetworkScanViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val nowLabel: () -> String = {
        SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US).format(Date())
    },
) : MviViewModel<NetworkScanUiState, NetworkScanAction, NetworkScanEvent>(NetworkScanUiState()) {
    override fun onAction(intent: NetworkScanAction) {
        when (intent) {
            NetworkScanAction.BackClicked -> { cancelScan(); sendEvent(NetworkScanEvent.Exit) }
            NetworkScanAction.SwitchWifiClicked -> sendEvent(NetworkScanEvent.OpenWifiSettings)
            NetworkScanAction.DevicesClicked -> sendEvent(NetworkScanEvent.OpenDevices)
            NetworkScanAction.WifiRefreshRequested -> refreshNetworkStateUntilWifiConnected()
            NetworkScanAction.ScanStartRequested -> startScan()

        }
    }

    private var networkRefreshJob: Job? = null
    private var scanJob: Job? = null

    init {
        refreshNetworkState()
    }

    fun refreshNetworkState(): Boolean {
        val hasWifi = repository.isWifiConnected()
        changeState {
            it.copy(
                networkInfo = repository.readNetworkInfo(),
                hasWifi = hasWifi,
                errorMessage = null,
            )
        }
        return hasWifi
    }

    private fun refreshNetworkStateUntilWifiConnected() {
        if (currentState.phase == NetworkScanPhase.Scanning) return
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

    private fun startScan() {
        val state = currentState
        if (state.phase == NetworkScanPhase.Scanning || !state.hasWifi) return

        networkRefreshJob?.cancel()
        scanJob?.cancel()
        NetworkScanSessionStore.clear()
        changeState { NetworkScanReducer.scanStarted(it) }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                runCatching {
                    val scan = repository.scanWifi()
                    NetworkScanSessionStore.save(scan)
                    repeat(SCAN_DETAIL_COUNT) { index ->
                        delay(PROGRESS_DELAY_MILLIS)
                        changeState { it.copy(completedDetailCount = index + 1) }
                    }
                    scan
                }.onSuccess { scan ->
                    changeState { NetworkScanReducer.scanCompleted(it, scan, nowLabel()) }
                }.onFailure { error ->
                    if (error is CancellationException) throw error
                    changeState { NetworkScanReducer.scanFailed(it, error.message) }
                }
            }
    }

    private fun cancelScan() {
        scanJob?.cancel()
        scanJob = null
        changeState { NetworkScanReducer.scanCancelled(it) }
    }

    private companion object {
        private const val SCAN_DETAIL_COUNT = 6
        private const val PROGRESS_DELAY_MILLIS = 260L
        private const val NETWORK_REFRESH_RETRY_COUNT = 12
        private const val NETWORK_REFRESH_RETRY_DELAY_MILLIS = 500L
    }
}

