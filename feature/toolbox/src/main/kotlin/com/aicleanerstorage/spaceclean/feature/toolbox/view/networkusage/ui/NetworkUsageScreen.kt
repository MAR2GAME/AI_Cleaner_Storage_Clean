package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageIntent


import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui.NetworkUsageScreenState

@Composable
fun NetworkUsageScreen(
    state: NetworkUsageUiState,
    onAction: (NetworkUsageIntent) -> Unit,
) {
    NetworkUsageScreenState(
        uiState = state,
        showScanning = state.isScanning,
        showStopDialog = state.showStopDialog,
        onBack = { onAction(NetworkUsageIntent.Back) },
        onTabSelected = { onAction(NetworkUsageIntent.TabSelected(it)) },
        onQuitScan = { onAction(NetworkUsageIntent.QuitScan) },
        onResumeScan = { onAction(NetworkUsageIntent.ResumeScan) },
    )
}

