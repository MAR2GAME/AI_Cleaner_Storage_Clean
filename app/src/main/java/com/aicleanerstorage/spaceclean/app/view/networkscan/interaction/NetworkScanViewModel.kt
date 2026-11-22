package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
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
            NetworkScanAction.BackClicked -> {
                if (currentState.mode == NetworkScanMode.Devices) {
                    setState { NetworkScanReducer.devicesClosed(it) }
                } else {
                    cancelScan()
                    sendEvent(NetworkScanEvent.Exit)
                }
            }
            NetworkScanAction.SwitchWifiClicked -> sendEvent(NetworkScanEvent.OpenWifiSettings)
            NetworkScanAction.DevicesClicked -> {
                setState { NetworkScanReducer.devicesOpened(it) }
                if (currentState.scan == null && currentState.phase != NetworkScanPhase.Scanning) {
                    startScan()
                }
            }
            NetworkScanAction.DevicesClosed -> setState { NetworkScanReducer.devicesClosed(it) }
            NetworkScanAction.DevicesRetryClicked -> startScan()
            NetworkScanAction.WifiRefreshRequested -> refreshNetworkStateUntilWifiConnected()
            NetworkScanAction.ScanStartRequested -> startScan()
        }
    }

    private var networkRefreshJob: Job? = null
    private var scanJob: Job? = null

    init {
        viewModelScope.launch(ioDispatcher) { refreshNetworkState() }
    }

    fun refreshNetworkState(): Boolean {
        val hasWifi = repository.isWifiConnected()
        setState {
            it.copy(
                networkInfo = repository.readNetworkInfo(),
                hasWifi = hasWifi,
                phase = if (it.phase == NetworkScanPhase.Error) NetworkScanPhase.Idle else it.phase,
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
        setState { NetworkScanReducer.scanStarted(it) }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                runCatching {
                    val scan = repository.scanWifi()
                    repeat(SCAN_DETAIL_COUNT) { index ->
                        delay(PROGRESS_DELAY_MILLIS)
                        setState { it.copy(completedDetailCount = index + 1) }
                    }
                    scan
                }.onSuccess { scan ->
                    setState { NetworkScanReducer.scanCompleted(it, scan, nowLabel()) }
                }.onFailure { error ->
                    if (error is CancellationException) throw error
                    setState { NetworkScanReducer.scanFailed(it) }
                }
            }
    }

    private fun cancelScan() {
        scanJob?.cancel()
        scanJob = null
        setState { NetworkScanReducer.scanCancelled(it) }
    }

    private companion object {
        private const val SCAN_DETAIL_COUNT = 6
        private const val PROGRESS_DELAY_MILLIS = 260L
        private const val NETWORK_REFRESH_RETRY_COUNT = 12
        private const val NETWORK_REFRESH_RETRY_DELAY_MILLIS = 500L
    }
}

