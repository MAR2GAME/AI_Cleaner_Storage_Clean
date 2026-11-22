package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel

internal object NetworkScanReducer {
    fun scanStarted(state: NetworkScanUiState): NetworkScanUiState =
        state.copy(
            phase = NetworkScanPhase.Scanning,
            scan = null,
            scanTime = "--",
            completedDetailCount = 0,
        )

    fun scanCompleted(
        state: NetworkScanUiState,
        scan: NetworkScanResultModel,
        time: String,
    ): NetworkScanUiState =
        if (state.phase != NetworkScanPhase.Scanning) {
            state
        } else {
            state.copy(
                phase = NetworkScanPhase.Result,
                scan = scan,
                scanTime = time,
                networkInfo =
                    state.networkInfo.copy(
                        ssid = scan.ssid ?: state.networkInfo.ssid,
                        ip = scan.deviceIp,
                    ),
                hasWifi = scan.hasWifi,
            )
        }

    fun scanFailed(state: NetworkScanUiState): NetworkScanUiState =
        if (state.phase != NetworkScanPhase.Scanning) {
            state
        } else {
            state.copy(
                phase = NetworkScanPhase.Error,
            )
        }

    fun devicesOpened(state: NetworkScanUiState): NetworkScanUiState = state.copy(mode = NetworkScanMode.Devices)

    fun devicesClosed(state: NetworkScanUiState): NetworkScanUiState = state.copy(mode = NetworkScanMode.Scan)

    fun scanCancelled(state: NetworkScanUiState): NetworkScanUiState =
        if (state.phase == NetworkScanPhase.Scanning) {
            state.copy(phase = NetworkScanPhase.Idle, completedDetailCount = 0)
        } else {
            state
        }
}
