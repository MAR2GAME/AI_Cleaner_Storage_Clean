package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanUiState
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanCardBg
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanDivider
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanNavy
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanNavyMuted

@Composable
internal fun NetworkScanResultCard(uiState: NetworkScanUiState) {
    NetworkScanSectionCard(title = stringResource(R.string.wifi_scan_result)) {
        NetworkScanInfoRow(
            label = stringResource(R.string.wifi_ssid_label),
            value = uiState.scan?.ssid ?: uiState.networkInfo.ssid ?: stringResource(R.string.unknown_ssid),
        )
        NetworkScanDivider()
        NetworkScanInfoRow(
            label = stringResource(R.string.last_scan),
            value = uiState.scanTime,
        )
    }
}

@Composable
internal fun NetworkScanSectionCard(
    title: String,
    verticalPadding: Dp = 20.dp,
    shapeRadius: Dp = NetworkScanCardRadius,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NetworkScanCardBg,
        shape = RoundedCornerShape(shapeRadius),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = verticalPadding),
        ) {
            Text(
                text = title,
                color = NetworkScanNavy,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
internal fun NetworkScanInfoRow(
    label: String,
    value: String,
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
        Text(
            text = value.ifBlank { "--" },
            color = NetworkScanNavy,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .padding(start = 16.dp)
                    .widthIn(max = 180.dp),
        )
    }
}

@Composable
internal fun NetworkScanDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        color = NetworkScanDivider,
        thickness = 1.dp,
    )
}
