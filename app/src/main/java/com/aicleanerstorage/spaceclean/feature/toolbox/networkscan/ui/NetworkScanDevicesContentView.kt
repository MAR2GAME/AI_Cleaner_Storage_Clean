package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanDevicesUiState

@Composable
internal fun NetworkScanDevicesContentView(
    uiState: NetworkScanDevicesUiState,
) {
    when {
        uiState.isLoading -> NetworkScanDevicesLoadingView(uiState = uiState)
        uiState.errorMessage != null -> NetworkScanDevicesErrorView(uiState = uiState)
        else -> NetworkScanDevicesResultView(uiState = uiState)
    }
}
