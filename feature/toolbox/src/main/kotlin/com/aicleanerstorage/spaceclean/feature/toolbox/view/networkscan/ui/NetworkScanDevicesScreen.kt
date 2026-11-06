package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesIntent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.core.ui.components.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.core.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanDevicesContentView
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget

@Composable
fun NetworkScanDevicesScreen(
    state: NetworkScanDevicesUiState,
    onAction: (NetworkScanDevicesIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {

    CleanXScaffoldPage(
        title = stringResource(R.string.network_scan),
        modifier = modifier,
        onBack = { onAction(NetworkScanDevicesIntent.Back) },
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
                        onClick = { onAction(NetworkScanDevicesIntent.Retry) },
                    )
                }
            }
        },
    ) {
        NetworkScanDevicesContentView(uiState = state)
    }
}

