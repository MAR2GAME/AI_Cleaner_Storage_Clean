package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.NetworkScanResultModel

data class NetworkScanDevicesUiState(
    val scan: NetworkScanResultModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val devices get() = scan?.devices.orEmpty()
}

sealed interface NetworkScanDevicesAction {
    data class NavigationRequested(val destination: AppDestination) : NetworkScanDevicesAction
    data object BackClicked : NetworkScanDevicesAction
    data object RetryClicked : NetworkScanDevicesAction

    data object DevicesLoadRequested : NetworkScanDevicesAction
}

sealed interface NetworkScanDevicesEvent {
    data class Navigate(val destination: AppDestination) : NetworkScanDevicesEvent
    data object NavigateBack : NetworkScanDevicesEvent
}
