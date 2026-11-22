package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel

enum class NetworkScanPhase {
    Idle,
    Scanning,
    Result,
    Error,
}

enum class NetworkScanMode {
    Scan,
    Devices,
}

data class NetworkScanUiState(
    val networkInfo: NetworkInfoModel = NetworkInfoModel(),
    val hasWifi: Boolean = false,
    val phase: NetworkScanPhase = NetworkScanPhase.Idle,
    val mode: NetworkScanMode = NetworkScanMode.Scan,
    val scan: NetworkScanResultModel? = null,
    val scanTime: String = "--",
    val completedDetailCount: Int = 0,
) {
    /** 单一真相：错误文案由 phase 派生，不再单独存一份。 */
    val error: UiText?
        get() = if (phase == NetworkScanPhase.Error) UiText.Res(R.string.scan_failed) else null
}

sealed interface NetworkScanEvent {
    data object Exit : NetworkScanEvent

    data object OpenWifiSettings : NetworkScanEvent
}

sealed interface NetworkScanAction {
    data object BackClicked : NetworkScanAction

    data object SwitchWifiClicked : NetworkScanAction

    data object DevicesClicked : NetworkScanAction

    data object DevicesClosed : NetworkScanAction

    data object DevicesRetryClicked : NetworkScanAction

    data object WifiRefreshRequested : NetworkScanAction

    data object ScanStartRequested : NetworkScanAction
}
