package com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.*


import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedUiState

@Composable
internal fun NetworkSpeedContentView(
    uiState: NetworkSpeedUiState,
    onNavigateTool: (com.aicleanerstorage.spaceclean.app.navigation.AppDestination) -> Unit,
) {
    when (uiState.phase) {
        NetworkSpeedPhase.Idle -> NetworkSpeedIdleView(uiState = uiState)
        NetworkSpeedPhase.Testing -> NetworkSpeedTestingView(uiState = uiState)
        NetworkSpeedPhase.Completing -> NetworkSpeedTestingView(uiState = uiState)
        NetworkSpeedPhase.Result -> NetworkSpeedResultView(uiState = uiState, onNavigateTool = onNavigateTool)
        NetworkSpeedPhase.Error -> NetworkSpeedErrorView(uiState = uiState)
    }
}
