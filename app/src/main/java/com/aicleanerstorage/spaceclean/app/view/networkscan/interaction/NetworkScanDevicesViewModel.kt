package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.networkkit.NetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkScanDevicesViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkScanDevicesUiState, NetworkScanDevicesAction, NetworkScanDevicesEvent>(NetworkScanDevicesUiState()) {
    init {
        loadDevices()
    }

    override fun onAction(action: NetworkScanDevicesAction) {
        when (action) {
            is NetworkScanDevicesAction.NavigationRequested -> sendEvent(NetworkScanDevicesEvent.Navigate(action.destination))
            NetworkScanDevicesAction.BackClicked -> sendEvent(NetworkScanDevicesEvent.NavigateBack)
            NetworkScanDevicesAction.RetryClicked -> loadDevices()
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

