package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedNavyMuted
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
internal fun NetworkSpeedResultContent(
    uiState: NetworkSpeedUiState,
    onNavigateTool: (AppDestination) -> Unit,
) {
    if (!uiState.speed.measured && (uiState.speed.downloadMbps != null || uiState.speed.uploadMbps != null)) {
        Text(text = stringResource(R.string.network_speed_estimated), color = NetworkSpeedNavyMuted)
        Spacer(modifier = Modifier.height(8.dp))
    }
    NetworkSpeedMetricCard(uiState = uiState)
    Spacer(modifier = Modifier.height(40.dp))
    ToolFeatureBanners(
        onNavigateTool = onNavigateTool,
        excludedDestinations = setOf(AppDestination.NetworkSpeed),
    )
    Spacer(modifier = Modifier.height(32.dp))
}
