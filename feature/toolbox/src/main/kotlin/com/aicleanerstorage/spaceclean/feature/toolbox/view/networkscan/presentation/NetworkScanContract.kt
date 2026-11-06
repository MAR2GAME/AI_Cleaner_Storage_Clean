package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkInfo

enum class NetworkScanPhase {
    Idle,
    Scanning,
    Result,
    Error,
}

data class NetworkScanUiState(
    val networkInfo: NetworkInfo = NetworkInfo(),
    val hasWifi: Boolean = false,
    val phase: NetworkScanPhase = NetworkScanPhase.Idle,
    val scan: NetworkScanResult? = null,
    val scanTime: String = "--",
    val completedDetailCount: Int = 0,
    val errorMessage: String? = null,
)

sealed interface NetworkScanEffect {
    data object Exit : NetworkScanEffect
    data object OpenWifiSettings : NetworkScanEffect
    data object OpenDevices : NetworkScanEffect
}

sealed interface NetworkScanIntent {
    data object Back : NetworkScanIntent
    data object SwitchWifi : NetworkScanIntent
    data object OpenDevices : NetworkScanIntent
    data object RefreshNetworkStateUntilWifiConnected : NetworkScanIntent
    data object StartScan : NetworkScanIntent
    data object CancelScan : NetworkScanIntent
}

internal data class NetworkScanStateChanged(val value: NetworkScanUiState) : NetworkScanIntent

