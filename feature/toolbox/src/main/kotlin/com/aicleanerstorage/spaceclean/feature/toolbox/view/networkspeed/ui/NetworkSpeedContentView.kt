package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedUiState


import androidx.compose.runtime.Composable

@Composable
fun NetworkSpeedContentView(
    uiState: NetworkSpeedUiState,
    onNavigateTool: (com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget) -> Unit,
) {
    when (uiState.phase) {
        NetworkSpeedPhase.Idle -> NetworkSpeedIdleView(uiState = uiState)
        NetworkSpeedPhase.Testing -> NetworkSpeedTestingView(uiState = uiState)
        NetworkSpeedPhase.Completing -> NetworkSpeedTestingView(uiState = uiState)
        NetworkSpeedPhase.Result -> NetworkSpeedResultView(uiState = uiState, onNavigateTool = onNavigateTool)
        NetworkSpeedPhase.Error -> NetworkSpeedErrorView(uiState = uiState)
    }
}

