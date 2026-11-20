package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkScanDevicesViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkScanDevicesUiState, NetworkScanDevicesAction, NetworkScanDevicesEvent>(NetworkScanDevicesUiState()) {
    override fun onAction(intent: NetworkScanDevicesAction) {
        when (intent) {
            is NetworkScanDevicesAction.NavigationRequested -> sendEvent(NetworkScanDevicesEvent.Navigate(intent.destination))
            NetworkScanDevicesAction.DevicesLoadRequested -> loadDevices()
            else -> handleIntent(intent)
        }
    }

    init {
        loadDevices()
    }

    private fun handleIntent(action: NetworkScanDevicesAction) {
        when (action) {
            NetworkScanDevicesAction.BackClicked -> sendEvent(NetworkScanDevicesEvent.NavigateBack)
            NetworkScanDevicesAction.RetryClicked -> loadDevices()
            else -> Unit
        }
    }

    private fun loadDevices() {
        val cachedScan = NetworkScanSessionStore.get()
        if (cachedScan != null) {
            setState { NetworkScanDevicesUiState(scan = cachedScan, isLoading = false) }
            return
        }

        changeState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                repository.scanWifi()
            }.onSuccess { scan ->
                NetworkScanSessionStore.save(scan)
                setState { NetworkScanDevicesUiState(scan = scan, isLoading = false) }
            }.onFailure { error ->
                setState { NetworkScanDevicesUiState(
                        isLoading = false,
                        errorMessage = error.message,
                    ) }
            }
        }
    }
}

