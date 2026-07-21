package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanResultUiState

private val ResultText = Color(0xFF282927)
private val ResultSubtext = Color(0xFF999999)
private val ResultAction = Color(0xFF5DD595)
private val ResultMutedText = Color(0xA6282927)

@Composable
internal fun JunkCleanResultView(
    uiState: JunkCleanResultUiState,
    onNavigateTool: (AppDestination) -> Unit,
) {
    val formatted = uiState.formattedFreedSpace
    val showResult = uiState.hasVisibleResult && formatted.isNotBlank()
    val numberPart = if (showResult) formatted.substringBeforeLast(" ", formatted) else "0"
    val unitPart = if (showResult) formatted.substringAfterLast(" ", "MB") else "MB"

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 175.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFD0FD91), ResultAction)),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = numberPart,
                    color = ResultText,
                    fontSize = 33.85.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unitPart,
                    color = ResultText,
                    fontSize = 18.46.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Text(
            text =
                if (uiState.failedCount > 0) {
                    stringResource(R.string.clean_result_junk_removed_with_failed, uiState.failedCount)
                } else {
                    stringResource(R.string.clean_result_junk_removed)
                },
            color = ResultSubtext,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 231.dp),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 284.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            JunkRecommendationCard(
                title = stringResource(R.string.nav_device_info),
                subtitle = stringResource(R.string.common_tool_device_desc),
                action = stringResource(R.string.view_now),
                destination = AppDestination.DeviceInfo,
                onNavigateTool = onNavigateTool,
                icon = {
                    DeviceInfoRecommendationIcon()
                },
            )
            JunkRecommendationCard(
                title = stringResource(R.string.nav_network_scan),
                subtitle = stringResource(R.string.network_scan),
                action = stringResource(R.string.scan_now),
                destination = AppDestination.NetworkScan,
                onNavigateTool = onNavigateTool,
                icon = {
                    NetworkScanRecommendationIcon()
                },
            )
        }
    }
}

@Composable
private fun JunkRecommendationCard(
    title: String,
    subtitle: String,
    action: String,
    destination: AppDestination,
    onNavigateTool: (AppDestination) -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(116.dp)
                .clickable { onNavigateTool(destination) },
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = title,
                        color = ResultText,
                        fontSize = 20.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = ResultMutedText,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(
                text = action,
                color = ResultAction,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun DeviceInfoRecommendationIcon() {
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.8.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF8D77FF), Color(0xFF5638FF)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_device),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(33.dp),
        )
    }
}

@Composable
private fun NetworkScanRecommendationIcon() {
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.8.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFFFC745), Color(0xFFFE9915)))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.junk_result_network_scan_figma),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .width(31.dp)
                    .height(25.dp)
                    .offset(y = (-1).dp),
        )
    }
}
