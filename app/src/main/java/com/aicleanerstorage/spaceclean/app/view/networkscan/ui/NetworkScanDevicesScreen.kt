package com.aicleanerstorage.spaceclean.app.view.networkscan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesAction
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanDevicesContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

@Composable
fun NetworkScanDevicesScreen(
    state: NetworkScanDevicesUiState,
    onAction: (NetworkScanDevicesAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {

    CleanXScaffoldPage(
        title = stringResource(R.string.network_scan),
        modifier = modifier,
        onBack = { onAction(NetworkScanDevicesAction.BackClicked) },
        bottomBar = {
            if (!state.isLoading && state.errorMessage != null) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .stableNavigationBarsPadding()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    CleanXPrimaryButton(
                        text = stringResource(R.string.retry),
                        onClick = { onAction(NetworkScanDevicesAction.RetryClicked) },
                    )
                }
            }
        },
    ) {
        NetworkScanDevicesContent(uiState = state)
    }
}
