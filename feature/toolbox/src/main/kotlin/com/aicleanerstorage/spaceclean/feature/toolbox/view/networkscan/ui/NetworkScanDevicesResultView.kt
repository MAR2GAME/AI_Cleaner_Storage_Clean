package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesUiState


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R

@Composable
fun NetworkScanDevicesResultView(uiState: NetworkScanDevicesUiState) {
    NetworkScanDevicesHeaderCard(count = uiState.devices.size)
    if (uiState.devices.isEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        NetworkScanMessageCard(
            title = stringResource(R.string.no_device_found),
            message = stringResource(R.string.no_devices_on_wifi),
        )
    } else {
        uiState.devices.forEach { device ->
            Spacer(modifier = Modifier.height(20.dp))
            NetworkScanDeviceDetailCard(device = device)
        }
    }
    Spacer(modifier = Modifier.height(96.dp))
}

