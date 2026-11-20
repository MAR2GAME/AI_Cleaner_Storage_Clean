package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.networkkit.model.NetworkScanResult

internal object NetworkScanReducer {
    fun scanStarted(state: NetworkScanUiState): NetworkScanUiState =
        state.copy(
            phase = NetworkScanPhase.Scanning,
            scan = null,
            scanTime = "--",
            completedDetailCount = 0,
            errorMessage = null,
        )

    fun scanCompleted(state: NetworkScanUiState, scan: NetworkScanResult, time: String): NetworkScanUiState =
        if (state.phase != NetworkScanPhase.Scanning) state
        else state.copy(
            phase = NetworkScanPhase.Result,
            scan = scan,
            scanTime = time,
            networkInfo =
                state.networkInfo.copy(
                    ssid = scan.ssid ?: state.networkInfo.ssid,
                    ip = scan.deviceIp,
                ),
            hasWifi = scan.hasWifi,
            errorMessage = null,
        )

    fun scanFailed(state: NetworkScanUiState, message: String?): NetworkScanUiState =
        if (state.phase != NetworkScanPhase.Scanning) state
        else state.copy(
            phase = NetworkScanPhase.Error,
            errorMessage = message,
        )

    fun scanCancelled(state: NetworkScanUiState): NetworkScanUiState =
        if (state.phase == NetworkScanPhase.Scanning) {
            state.copy(phase = NetworkScanPhase.Idle, completedDetailCount = 0, errorMessage = null)
        } else {
            state
        }
}
