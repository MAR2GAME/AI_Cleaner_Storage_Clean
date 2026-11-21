package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.networkkit.model.NetworkTransport
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedCardBg
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedDivider
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedNavy
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedNavyMuted

@Composable
internal fun NetworkSpeedInfoCard(uiState: NetworkSpeedUiState) {
    val showNetworkInfo =
        uiState.hasNetwork &&
            (uiState.phase == NetworkSpeedPhase.Testing || uiState.phase == NetworkSpeedPhase.Completing)

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(154.dp),
        color = NetworkSpeedCardBg,
        shape = RoundedCornerShape(NetworkSpeedCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            NetworkSpeedInfoLine(
                label = stringResource(R.string.network_type),
                value =
                    when (uiState.networkInfo.transport.takeIf { showNetworkInfo }) {
                        NetworkTransport.Wifi -> stringResource(R.string.wifi)
                        NetworkTransport.Cellular -> stringResource(R.string.network_type_cellular)
                        NetworkTransport.Ethernet -> stringResource(R.string.network_type_ethernet)
                        NetworkTransport.Unknown, null -> "--"
                    },
            )
            NetworkSpeedInfoDivider()
            NetworkSpeedInfoLine(
                label = stringResource(R.string.wifi_name),
                value = if (showNetworkInfo) uiState.networkInfo.ssid ?: stringResource(R.string.unknown_ssid) else "--",
            )
            NetworkSpeedInfoDivider()
            NetworkSpeedInfoLine(
                label = stringResource(R.string.ip),
                value = if (showNetworkInfo) uiState.networkInfo.ip ?: "--" else "--",
            )
        }
    }
}

@Composable
private fun NetworkSpeedInfoLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(39.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = NetworkSpeedNavyMuted,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = value.ifBlank { "--" },
            color = NetworkSpeedNavy,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
private fun NetworkSpeedInfoDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 0.dp),
        color = NetworkSpeedDivider,
        thickness = 1.dp,
    )
}
