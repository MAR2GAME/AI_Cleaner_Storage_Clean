package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanUiState

@Composable
internal fun NetworkScanContentView(
    uiState: NetworkScanUiState,
    onDevicesClick: () -> Unit,
) {
    when (uiState.phase) {
        NetworkScanPhase.Idle -> NetworkScanIdleView(uiState = uiState)
        NetworkScanPhase.Scanning -> NetworkScanScanningView(uiState = uiState)
        NetworkScanPhase.Result -> NetworkScanResultView(
            uiState = uiState,
            onDevicesClick = onDevicesClick,
        )
        NetworkScanPhase.Error -> NetworkScanErrorView(uiState = uiState)
    }
}
