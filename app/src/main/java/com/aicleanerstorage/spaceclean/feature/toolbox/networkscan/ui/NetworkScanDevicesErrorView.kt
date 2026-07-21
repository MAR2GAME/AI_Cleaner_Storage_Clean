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
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanDevicesUiState

@Composable
internal fun NetworkScanDevicesErrorView(
    uiState: NetworkScanDevicesUiState,
) {
    NetworkScanDevicesHeaderCard(count = uiState.devices.size)
    Spacer(modifier = Modifier.height(20.dp))
    NetworkScanMessageCard(
        title = stringResource(R.string.scan_failed),
        message = uiState.errorMessage.orEmpty(),
    )
}
