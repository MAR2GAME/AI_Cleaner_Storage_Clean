package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
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
    private val networkInfoReader: NetworkInfoReader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val nowLabel: () -> String = {
        SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US).format(Date())
    },
) : MviViewModel<NetworkScanUiState, NetworkScanIntent, NetworkScanEffect>(NetworkScanUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: NetworkScanIntent) {
        when (intent) {
            NetworkScanIntent.Back -> { cancelScan(); emitEffect(NetworkScanEffect.Exit) }
            NetworkScanIntent.SwitchWifi -> emitEffect(NetworkScanEffect.OpenWifiSettings)
            NetworkScanIntent.OpenDevices -> emitEffect(NetworkScanEffect.OpenDevices)
            NetworkScanIntent.RefreshNetworkStateUntilWifiConnected -> refreshNetworkStateUntilWifiConnected()
            NetworkScanIntent.StartScan -> startScan()
            NetworkScanIntent.CancelScan -> cancelScan()
            else -> {
        if (intent is NetworkScanStateChanged) {
            updateState { NetworkScanReducer.reduce(this, intent) }
        } else {
            Unit
        }
    
            }
        }
    }

    private fun publishState(value: NetworkScanUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkScanStateChanged(value)) }
    }

    private fun changeState(transform: (NetworkScanUiState) -> NetworkScanUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkScanStateChanged(transform(currentState))) }
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
                networkInfo = networkInfoReader.read(),
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
        changeState {
            it.copy(
                phase = NetworkScanPhase.Scanning,
                scan = null,
                scanTime = "--",
                completedDetailCount = 0,
                errorMessage = null,
            )
        }
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
                    changeState {
                        it.copy(
                            phase = NetworkScanPhase.Result,
                            scan = scan,
                            scanTime = nowLabel(),
                            networkInfo =
                                it.networkInfo.copy(
                                    ssid = scan.ssid.takeUnless { ssid -> ssid == "<unknown ssid>" } ?: it.networkInfo.ssid,
                                    ip = scan.deviceIp,
                                ),
                            hasWifi = scan.hasWifi,
                            errorMessage = null,
                        )
                    }
                }.onFailure { error ->
                    if (error is CancellationException) throw error
                    changeState {
                        it.copy(
                            phase = NetworkScanPhase.Error,
                            errorMessage = error.message,
                        )
                    }
                }
            }
    }

    private fun cancelScan() {
        scanJob?.cancel()
        scanJob = null
        changeState {
            if (it.phase == NetworkScanPhase.Scanning) {
                it.copy(phase = NetworkScanPhase.Idle, completedDetailCount = 0, errorMessage = null)
            } else {
                it
            }
        }
    }

    private companion object {
        private const val SCAN_DETAIL_COUNT = 6
        private const val PROGRESS_DELAY_MILLIS = 260L
        private const val NETWORK_REFRESH_RETRY_COUNT = 12
        private const val NETWORK_REFRESH_RETRY_DELAY_MILLIS = 500L
    }
}

