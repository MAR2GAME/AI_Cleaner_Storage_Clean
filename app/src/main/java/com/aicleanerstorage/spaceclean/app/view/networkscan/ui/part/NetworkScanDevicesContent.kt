package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesUiState

@Composable
internal fun NetworkScanDevicesContent(uiState: NetworkScanDevicesUiState) {
    when {
        uiState.isLoading -> NetworkScanDevicesLoading(uiState = uiState)
        uiState.error != null -> NetworkScanDevicesError(uiState = uiState)
        else -> NetworkScanDevicesResultContent(uiState = uiState)
    }
}
