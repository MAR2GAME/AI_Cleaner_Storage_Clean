package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanCardBg
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanDivider
import com.aicleanerstorage.spaceclean.domain.model.NetworkDeviceInfoModel

@Composable
internal fun NetworkScanDeviceDetailCard(device: NetworkDeviceInfoModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NetworkScanCardBg,
        shape = RoundedCornerShape(NetworkScanCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            NetworkScanDeviceSummaryRow(device = device)
            NetworkScanDivider()
            NetworkScanInfoRow(
                label = stringResource(R.string.vendor),
                value = stringResource(R.string.unknown),
            )
            NetworkScanDivider()
            NetworkScanInfoRow(
                label = stringResource(R.string.name),
                value = device.hostName.displayUnknown(),
            )
            NetworkScanDivider()
            NetworkScanInfoRow(
                label = stringResource(R.string.mac_address),
                value = device.macAddress.displayUnknown(),
            )
        }
    }
}
