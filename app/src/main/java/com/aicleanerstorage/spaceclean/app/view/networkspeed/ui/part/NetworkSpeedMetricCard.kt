package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

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
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedCardBg
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedDivider
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedGreen
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedGreenLight
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedNavy
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedNavyMuted
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.RotatingRingAnimation

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
