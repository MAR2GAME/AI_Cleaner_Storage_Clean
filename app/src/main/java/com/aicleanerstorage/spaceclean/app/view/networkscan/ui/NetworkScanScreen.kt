package com.aicleanerstorage.spaceclean.app.view.networkscan.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanBottomBar
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage

@Composable
fun NetworkScanScreen(
    uiState: NetworkScanUiState,
    onBack: () -> Unit,
    onRefreshWifi: () -> Unit,
    onSwitchWifi: () -> Unit,
    onScan: () -> Unit,
    onOpenDevices: () -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.network_scan),
        onBack = onBack,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(NetworkScanPageBg, NetworkScanPageBg),
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        bottomBar = {
            NetworkScanBottomBar(
                uiState = uiState,
                onRefreshWifi = onRefreshWifi,
                onSwitchWifi = onSwitchWifi,
                onScan = onScan,
            )
        },
    ) {
        NetworkScanContent(
            uiState = uiState,
            onDevicesClick = onOpenDevices,
        )
    }
}
