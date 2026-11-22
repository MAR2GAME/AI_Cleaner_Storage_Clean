package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState

@Composable
internal fun NetworkSpeedTestingContent(uiState: NetworkSpeedUiState) {
    NetworkSpeedInfoCard(uiState = uiState)
    Spacer(modifier = Modifier.height(20.dp))
    NetworkSpeedMetricCard(
        uiState = uiState,
        isDownloadTesting = uiState.speed.phase == SpeedTestPhase.Latency || uiState.speed.phase == SpeedTestPhase.Download,
        isUploadTesting = uiState.speed.phase == SpeedTestPhase.Upload,
    )
    Spacer(modifier = Modifier.height(22.dp))
    NetworkSpeedGaugePanel(isAnimating = true)
    Spacer(modifier = Modifier.height(8.dp))
}
