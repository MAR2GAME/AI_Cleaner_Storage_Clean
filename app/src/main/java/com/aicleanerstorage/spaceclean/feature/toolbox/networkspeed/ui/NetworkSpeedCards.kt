package com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.RotatingRingAnimation
import com.aicleanerstorage.spaceclean.common.ui.components.animations.SemiCircularGauge
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedUiState

@Composable
internal fun NetworkSpeedInfoCard(uiState: NetworkSpeedUiState) {
    val showNetworkInfo =
        uiState.hasNetwork &&
            (uiState.phase == NetworkSpeedPhase.Testing || uiState.phase == NetworkSpeedPhase.Completing)

    Surface(
        modifier = Modifier
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
                value = if (showNetworkInfo) uiState.networkInfo.type else "--",
            )
            NetworkSpeedInfoDivider()
            NetworkSpeedInfoLine(
                label = stringResource(R.string.wifi_name),
                value = if (showNetworkInfo) uiState.networkInfo.ssid else "--",
            )
            NetworkSpeedInfoDivider()
            NetworkSpeedInfoLine(
                label = stringResource(R.string.ip),
                value = if (showNetworkInfo) uiState.networkInfo.ip else "--",
            )
        }
    }
}

@Composable
internal fun NetworkSpeedMetricCard(
    uiState: NetworkSpeedUiState,
    isDownloadTesting: Boolean = false,
    isUploadTesting: Boolean = false,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        color = NetworkSpeedCardBg,
        shape = RoundedCornerShape(NetworkSpeedCardRadius),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NetworkSpeedMetricColumn(
                label = "${stringResource(R.string.download)} ${stringResource(R.string.mbps)}",
                value = uiState.downloadLabel,
                isTesting = isDownloadTesting,
                modifier = Modifier.weight(1f),
            )
            NetworkSpeedVerticalDivider()
            NetworkSpeedMetricColumn(
                label = "${stringResource(R.string.upload)} ${stringResource(R.string.mbps)}",
                value = uiState.uploadLabel,
                isTesting = isUploadTesting,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun NetworkSpeedGauge(isAnimating: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        SemiCircularGauge(
            modifier = Modifier.size(280.dp),
            isAnimating = isAnimating,
            arcStartColor = NetworkSpeedGreenLight,
            arcEndColor = NetworkSpeedGreen,
            needleColor = NetworkSpeedGreenDeep,
            tickColor = NetworkSpeedGreen.copy(alpha = 0.26f),
        )
    }
}

@Composable
internal fun NetworkSpeedEmptyCard(
    title: String,
    message: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NetworkSpeedCardBg,
        shape = RoundedCornerShape(NetworkSpeedCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = NetworkSpeedNavy,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = NetworkSpeedNavyMuted,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
internal fun NetworkSpeedErrorText(message: String?) {
    if (message.isNullOrBlank()) return

    Text(
        text = message,
        color = NetworkSpeedDanger,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
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

@Composable
private fun NetworkSpeedMetricColumn(
    label: String,
    value: String,
    isTesting: Boolean,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.height(39.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isTesting) {
                RotatingRingAnimation(
                    modifier =
                        Modifier
                            .size(34.dp),
                    ringWidth = 4.dp,
                    ringColor = NetworkSpeedGreen,
                    backgroundColor = NetworkSpeedGreenLight.copy(alpha = 0.36f),
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = value,
                        color = NetworkSpeedNavy,
                        fontSize = if (value == "--") 30.sp else 38.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.mbps),
                        color = NetworkSpeedNavy,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        maxLines = 1,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = NetworkSpeedNavyMuted,
            fontSize = 14.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NetworkSpeedVerticalDivider() {
    Box(
        modifier =
            Modifier
                .width(1.dp)
                .height(24.dp)
                .background(NetworkSpeedDivider),
    )
}
