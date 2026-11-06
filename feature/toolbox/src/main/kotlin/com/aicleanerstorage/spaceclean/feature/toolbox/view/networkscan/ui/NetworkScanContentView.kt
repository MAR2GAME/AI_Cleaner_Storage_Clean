package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanUiState


import androidx.compose.runtime.Composable

@Composable
fun NetworkScanContentView(
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

