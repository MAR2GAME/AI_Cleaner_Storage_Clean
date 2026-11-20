package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanPhase
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanUiState

@Composable
internal fun NetworkScanContent(
    uiState: NetworkScanUiState,
    onDevicesClick: () -> Unit,
) {
    when (uiState.phase) {
        NetworkScanPhase.Idle -> NetworkScanIdleContent(uiState = uiState)
        NetworkScanPhase.Scanning -> NetworkScanLoading(uiState = uiState)
        NetworkScanPhase.Result -> NetworkScanResultContent(
            uiState = uiState,
            onDevicesClick = onDevicesClick,
        )
        NetworkScanPhase.Error -> NetworkScanError(uiState = uiState)
    }
}
