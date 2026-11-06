package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkScanDevicesViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkScanDevicesUiState, NetworkScanDevicesIntent, NetworkScanDevicesEffect>(NetworkScanDevicesUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: NetworkScanDevicesIntent) {
        when (intent) {
            is NetworkScanDevicesIntent.Navigate -> emitEffect(NetworkScanDevicesEffect.Navigate(intent.destination))
            NetworkScanDevicesIntent.LoadDevices -> loadDevices()
            else -> {
        if (intent is NetworkScanDevicesStateChanged) {
            updateState { NetworkScanDevicesReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: NetworkScanDevicesUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkScanDevicesStateChanged(value)) }
    }

    private fun changeState(transform: (NetworkScanDevicesUiState) -> NetworkScanDevicesUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkScanDevicesStateChanged(transform(currentState))) }
    }


    init {
        loadDevices()
    }

    private fun handleIntent(action: NetworkScanDevicesIntent) {
        when (action) {
            NetworkScanDevicesIntent.Back -> emitEffect(NetworkScanDevicesEffect.Back)
            NetworkScanDevicesIntent.Retry -> loadDevices()
            is NetworkScanDevicesStateChanged -> Unit
            else -> Unit
        }
    }

    private fun loadDevices() {
        val cachedScan = NetworkScanSessionStore.get()
        if (cachedScan != null) {
            publishState(NetworkScanDevicesUiState(scan = cachedScan, isLoading = false))
            return
        }

        changeState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                repository.scanWifi()
            }.onSuccess { scan ->
                NetworkScanSessionStore.save(scan)
                publishState(NetworkScanDevicesUiState(scan = scan, isLoading = false))
            }.onFailure { error ->
                publishState(NetworkScanDevicesUiState(
                        isLoading = false,
                        errorMessage = error.message,
                    ))
            }
        }
    }
}

