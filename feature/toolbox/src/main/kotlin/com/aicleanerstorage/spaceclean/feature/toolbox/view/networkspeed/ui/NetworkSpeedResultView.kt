package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedUiState


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.components.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget

@Composable
fun NetworkSpeedResultView(
    uiState: NetworkSpeedUiState,
    onNavigateTool: (ToolNavigationTarget) -> Unit,
) {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        CommonResultCheckIcon()
//        Spacer(modifier = Modifier.height(20.dp))
//        Text(
//            text = stringResource(R.string.speed_test_complete),
//            color = NetworkSpeedNavyMuted,
//            fontSize = 16.sp,
//            lineHeight = 20.sp,
//            textAlign = TextAlign.Center,
//        )
//        Spacer(modifier = Modifier.height(22.dp))
//        NetworkSpeedMetricCard(uiState = uiState)
//    }
    NetworkSpeedMetricCard(uiState = uiState)
    Spacer(modifier = Modifier.height(40.dp))
    ToolFeatureBanners(
        onNavigateTool = onNavigateTool,
        excludedDestinations = setOf(ToolNavigationTarget.NetworkSpeed),
    )
    Spacer(modifier = Modifier.height(32.dp))
}

