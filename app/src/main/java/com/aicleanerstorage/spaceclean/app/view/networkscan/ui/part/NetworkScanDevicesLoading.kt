package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesUiState

@Composable
internal fun NetworkScanDevicesLoading(uiState: NetworkScanDevicesUiState) {
    NetworkScanDevicesHeaderCard(count = uiState.devices.size)
    Spacer(modifier = Modifier.height(20.dp))
    NetworkScanMessageCard(
        title = stringResource(R.string.scanning_devices),
        message = stringResource(R.string.scan_loading_fallback),
    )
    Spacer(modifier = Modifier.height(96.dp))
}
