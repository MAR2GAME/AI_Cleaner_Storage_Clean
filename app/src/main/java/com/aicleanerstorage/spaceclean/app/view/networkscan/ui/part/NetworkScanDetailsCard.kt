package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanPhase
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanDivider
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanNavyMuted
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanStatusGreen
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanStatusTrack
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXStatusBadge
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXStatusBadgeState

@Composable
internal fun NetworkScanDetailsCard(uiState: NetworkScanUiState) {
    val detailItems =
        listOf(
            stringResource(R.string.wifi_auth),
            stringResource(R.string.arp_spoofing),
            stringResource(R.string.ssl_stripping),
            stringResource(R.string.ssl_splitting),
            stringResource(R.string.dns_spoofing),
            stringResource(R.string.devices),
        )
    val completedCount =
        when (uiState.phase) {
            NetworkScanPhase.Result -> detailItems.size
            NetworkScanPhase.Scanning -> uiState.completedDetailCount
            else -> 0
        }

    NetworkScanSectionCard(
        title = stringResource(R.string.details),
        verticalPadding = 18.dp,
        shapeRadius = 12.dp,
    ) {
        detailItems.forEachIndexed { index, label ->
            if (index > 0) NetworkScanDivider()
            NetworkScanDetailRow(
                label = label,
                status =
                    when {
                        index < completedCount -> NetworkScanDetailStatus.Complete
                        uiState.phase == NetworkScanPhase.Scanning && index == completedCount -> NetworkScanDetailStatus.Active
                        else -> NetworkScanDetailStatus.Unknown
                    },
            )
        }
    }
}

private enum class NetworkScanDetailStatus {
    Unknown,
    Active,
    Complete,
}

@Composable
private fun NetworkScanDetailRow(
    label: String,
    status: NetworkScanDetailStatus,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = NetworkScanNavyMuted,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        when (status) {
            NetworkScanDetailStatus.Unknown ->
                Image(
                    painter = painterResource(id = R.drawable.ic_network_scan_unknown),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            NetworkScanDetailStatus.Active ->
                CleanXStatusBadge(
                    state = CleanXStatusBadgeState.Active,
                    ringColor = NetworkScanStatusGreen,
                    backgroundColor = NetworkScanStatusTrack,
                )
            NetworkScanDetailStatus.Complete ->
                CleanXStatusBadge(
                    state = CleanXStatusBadgeState.Complete,
                )
        }
    }
}
