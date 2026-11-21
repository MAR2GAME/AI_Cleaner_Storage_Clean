package com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part

import com.aicleanerstorage.spaceclean.base.util.FormatUtil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.devicekit.model.StorageInfo

@Composable
internal fun OnboardingResultContent(
    rows: List<DeviceScanRow>,
    storageInfo: StorageInfo,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        StorageCard(storageInfo = storageInfo)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.onboarding_device_info_disclaimer),
            color = OnboardingMuted,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(28.dp))
        DeviceRows(
            rows = rows,
            cardHeight = 377.dp,
        )
        Spacer(modifier = Modifier.height(31.dp))
        GetStartedButton(onClick = onGetStarted)
    }
}

private val OnboardingMuted = Color(0xFF999999)

private val OnboardingProgressTrack = Color(0xFFEEF4F9)

@Composable
private fun StorageCard(storageInfo: StorageInfo) {
    val usedText = compactStorageSize(FormatUtil.formatSize(storageInfo.usedBytes))
    val totalText = " / ${compactStorageSize(FormatUtil.formatSize(storageInfo.totalBytes))}"
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val totalTextStyle =
        TextStyle(
            color = OnboardingMuted,
            fontSize = 16.sp,
            lineHeight = 23.sp,
        )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
    ) {
        val isCompactCard = maxWidth < 340.dp
        val imageWidth = if (isCompactCard) 76.dp else 88.dp
        val imageHeight = if (isCompactCard) 65.dp else 75.dp
        val textEndPadding = if (isCompactCard) 8.dp else 12.dp

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = textEndPadding),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.home_storage_label),
                        color = OnboardingText,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.widthIn(max = 68.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BoxWithConstraints(modifier = Modifier.weight(1f)) {
                        val maxWidthPx = with(density) { maxWidth.roundToPx() }
                        val measuredTotalWidthPx =
                            remember(totalText, textMeasurer) {
                                textMeasurer.measure(
                                    text = totalText,
                                    style = totalTextStyle,
                                    maxLines = 1,
                                ).size.width
                            }
                        val totalWidthPx = measuredTotalWidthPx.coerceAtMost(maxWidthPx)
                        val usedWidthPx = (maxWidthPx - totalWidthPx).coerceAtLeast(0)
                        val usedFontSize =
                            remember(usedText, usedWidthPx, textMeasurer) {
                                (24 downTo 16).firstOrNull { size ->
                                    textMeasurer.measure(
                                        text = usedText,
                                        style =
                                            TextStyle(
                                                fontSize = size.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                        maxLines = 1,
                                    ).size.width <= usedWidthPx
                                }?.sp ?: 16.sp
                            }
                        val totalWidth = with(density) { totalWidthPx.toDp() }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = usedText,
                                color = OnboardingGreen,
                                fontSize = usedFontSize,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = totalText,
                                style = totalTextStyle,
                                modifier = Modifier.width(totalWidth),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(14.5.dp))
                        .background(OnboardingProgressTrack),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((storageInfo.usagePercent.coerceIn(0, 100)) / 100f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(14.5.dp))
                            .background(OnboardingGreen),
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.onboarding_trash_can),
                contentDescription = null,
                modifier = Modifier.size(width = imageWidth, height = imageHeight),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private fun compactStorageSize(value: String): String = value.replace(" ", "")

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFD0FD91), OnboardingGreen),
                ),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.onboarding_get_started),
            color = OnboardingText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
