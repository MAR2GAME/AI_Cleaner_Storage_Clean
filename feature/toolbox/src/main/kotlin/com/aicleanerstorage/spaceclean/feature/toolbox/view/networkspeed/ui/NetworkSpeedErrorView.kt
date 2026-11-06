package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedUiState


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R

@Composable
fun NetworkSpeedErrorView(uiState: NetworkSpeedUiState) {
    NetworkSpeedInfoCard(uiState = uiState)
    Spacer(modifier = Modifier.height(20.dp))
    if (uiState.hasNetwork) {
        NetworkSpeedMetricCard(uiState = uiState)
    } else {
        NetworkSpeedEmptyCard(
            title = stringResource(R.string.no_network_connection),
            message = stringResource(R.string.network_speed_no_connection_desc),
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    NetworkSpeedErrorText(message = uiState.errorMessage)
    Spacer(modifier = Modifier.height(96.dp))
}

