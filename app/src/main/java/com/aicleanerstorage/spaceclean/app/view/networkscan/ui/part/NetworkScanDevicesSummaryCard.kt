package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.networkkit.model.NetworkDeviceInfo
import com.aicleanerstorage.networkkit.model.NetworkScanResult
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanCardBg
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanDivider
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanNavy

@Composable
internal fun NetworkScanDevicesSummaryCard(
    scan: NetworkScanResult,
    onDevicesClick: () -> Unit,
) {
    val devices = scan.devices
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = devices.isNotEmpty(), onClick = onDevicesClick),
        color = NetworkScanCardBg,
        shape = RoundedCornerShape(NetworkScanCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.devices_count, devices.size),
                    color = NetworkScanNavy,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (devices.isNotEmpty()) NetworkScanNavy else Color.Transparent,
                )
            }
            if (devices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                devices.take(2).forEachIndexed { index, device ->
                    if (index > 0) NetworkScanDivider()
                    NetworkScanDeviceSummaryRow(device = device)
                }
            }
        }
    }
}

@Composable
internal fun NetworkScanDeviceSummaryRow(device: NetworkDeviceInfo) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = NetworkScanNavy,
            )
            Text(
                text = device.ip,
                color = NetworkScanNavy,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = device.hostName.displayUnknown(),
            color = NetworkScanNavy,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .widthIn(max = 112.dp),
        )
    }
}

@Composable
internal fun String?.displayUnknown(): String = this?.takeIf { it.isNotBlank() } ?: stringResource(R.string.unknown)
