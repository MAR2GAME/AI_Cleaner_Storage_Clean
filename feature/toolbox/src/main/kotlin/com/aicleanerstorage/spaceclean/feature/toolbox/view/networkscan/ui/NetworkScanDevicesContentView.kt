package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesUiState


import androidx.compose.runtime.Composable

@Composable
fun NetworkScanDevicesContentView(
    uiState: NetworkScanDevicesUiState,
) {
    when {
        uiState.isLoading -> NetworkScanDevicesLoadingView(uiState = uiState)
        uiState.errorMessage != null -> NetworkScanDevicesErrorView(uiState = uiState)
        else -> NetworkScanDevicesResultView(uiState = uiState)
    }
}

