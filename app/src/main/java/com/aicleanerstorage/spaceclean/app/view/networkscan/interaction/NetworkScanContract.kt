package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.domain.model.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkInfoModel

enum class NetworkScanPhase {
    Idle,
    Scanning,
    Result,
    Error,
}

data class NetworkScanUiState(
    val networkInfo: NetworkInfoModel = NetworkInfoModel(),
    val hasWifi: Boolean = false,
    val phase: NetworkScanPhase = NetworkScanPhase.Idle,
    val scan: NetworkScanResultModel? = null,
    val scanTime: String = "--",
    val completedDetailCount: Int = 0,
    val errorMessage: String? = null,
)

sealed interface NetworkScanEvent {
    data object Exit : NetworkScanEvent
    data object OpenWifiSettings : NetworkScanEvent
    data object OpenDevices : NetworkScanEvent
}

sealed interface NetworkScanAction {
    data object BackClicked : NetworkScanAction
    data object SwitchWifiClicked : NetworkScanAction
    data object DevicesClicked : NetworkScanAction
    data object WifiRefreshRequested : NetworkScanAction
    data object ScanStartRequested : NetworkScanAction
    data object ScanCancellationRequested : NetworkScanAction
}
