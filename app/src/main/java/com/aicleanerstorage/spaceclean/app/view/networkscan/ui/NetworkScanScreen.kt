package com.aicleanerstorage.spaceclean.app.view.networkscan.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanAction
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanMode
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanPhase
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanBottomBar
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanContent
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanDevicesContent
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanPageBg
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
fun NetworkScanScreen(
    uiState: NetworkScanUiState,
    onAction: (NetworkScanAction) -> Unit,
) {
    BackHandler { onAction(NetworkScanAction.BackClicked) }

    val devicesMode = uiState.mode == NetworkScanMode.Devices
    val devicesState =
        NetworkScanDevicesUiState(
            scan = uiState.scan,
            isLoading = uiState.phase == NetworkScanPhase.Scanning,
            error = uiState.error,
        )

    CleanXScaffoldPage(
        title = stringResource(R.string.network_scan),
        onBack = { onAction(NetworkScanAction.BackClicked) },
        backgroundBrush =
            Brush.linearGradient(
                colors = listOf(NetworkScanPageBg, NetworkScanPageBg),
            ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        bottomBar = {
            if (devicesMode) {
                if (uiState.error != null && uiState.phase != NetworkScanPhase.Scanning) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .stableNavigationBarsPadding()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    ) {
                        CleanXPrimaryButton(
                            text = stringResource(R.string.retry),
                            onClick = { onAction(NetworkScanAction.DevicesRetryClicked) },
                        )
                    }
                }
            } else {
                NetworkScanBottomBar(
                    uiState = uiState,
                    onRefreshWifi = { onAction(NetworkScanAction.WifiRefreshRequested) },
                    onSwitchWifi = { onAction(NetworkScanAction.SwitchWifiClicked) },
                    onScan = { onAction(NetworkScanAction.ScanStartRequested) },
                )
            }
        },
    ) {
        if (devicesMode) {
            NetworkScanDevicesContent(uiState = devicesState)
        } else {
            NetworkScanContent(
                uiState = uiState,
                onDevicesClick = { onAction(NetworkScanAction.DevicesClicked) },
            )
        }
    }
}
