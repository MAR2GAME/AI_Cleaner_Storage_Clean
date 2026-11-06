package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult

data class NetworkScanDevicesUiState(
    val scan: NetworkScanResult? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val devices get() = scan?.devices.orEmpty()
}

sealed interface NetworkScanDevicesIntent {
    data class Navigate(val destination: AppDestination) : NetworkScanDevicesIntent
    data object Back : NetworkScanDevicesIntent
    data object Retry : NetworkScanDevicesIntent

    data object LoadDevices : NetworkScanDevicesIntent
}

sealed interface NetworkScanDevicesEffect {
    data class Navigate(val destination: AppDestination) : NetworkScanDevicesEffect
 data object Back : NetworkScanDevicesEffect }

internal data class NetworkScanDevicesStateChanged(val value: NetworkScanDevicesUiState) : NetworkScanDevicesIntent

