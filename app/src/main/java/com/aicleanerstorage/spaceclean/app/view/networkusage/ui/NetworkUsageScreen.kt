package com.aicleanerstorage.spaceclean.app.view.networkusage.ui

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageAction
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageUiState
import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.part.NetworkUsageContent

@Composable
fun NetworkUsageScreen(
    state: NetworkUsageUiState,
    onAction: (NetworkUsageAction) -> Unit,
) {
    NetworkUsageContent(
        uiState = state,
        showScanning = state.isScanning,
        showStopDialog = state.showStopDialog,
        onBack = { onAction(NetworkUsageAction.BackClicked) },
        onTabSelected = { onAction(NetworkUsageAction.TabSelected(it)) },
        onQuitScan = { onAction(NetworkUsageAction.QuitScanConfirmed) },
        onResumeScan = { onAction(NetworkUsageAction.ResumeScanClicked) },
    )
}
