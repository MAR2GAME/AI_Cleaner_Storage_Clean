package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanUiState

@Composable
internal fun NetworkScanErrorView(uiState: NetworkScanUiState) {
    NetworkScanResultCard(uiState = uiState)
    Spacer(modifier = Modifier.height(20.dp))
    NetworkScanDetailsCard(uiState = uiState)
    if (!uiState.hasWifi) {
        Spacer(modifier = Modifier.height(20.dp))
        NetworkScanMessageCard(
            title = stringResource(R.string.wifi_not_connected),
            message = stringResource(R.string.network_scan_no_wifi_desc),
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    NetworkScanErrorText(message = uiState.errorMessage)
    Spacer(modifier = Modifier.height(96.dp))
}
