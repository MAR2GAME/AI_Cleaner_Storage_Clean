package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState

@Composable
internal fun NetworkSpeedContent(
    uiState: NetworkSpeedUiState,
    onNavigateTool: (com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget) -> Unit,
) {
    when (uiState.phase) {
        NetworkSpeedPhase.Idle -> NetworkSpeedIdleContent(uiState = uiState)
        NetworkSpeedPhase.Testing -> NetworkSpeedTestingContent(uiState = uiState)
        NetworkSpeedPhase.Completing -> NetworkSpeedTestingContent(uiState = uiState)
        NetworkSpeedPhase.Result -> NetworkSpeedResultContent(uiState = uiState, onNavigateTool = onNavigateTool)
        NetworkSpeedPhase.Error -> NetworkSpeedError(uiState = uiState)
    }
}
