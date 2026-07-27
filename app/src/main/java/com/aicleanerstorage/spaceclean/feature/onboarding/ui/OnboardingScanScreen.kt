package com.aicleanerstorage.spaceclean.feature.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.animations.RotatingRingAnimation
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingAction
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.device.model.StorageInfo

private val OnboardingBackground = Color(0xFFF4F4F4)
private val OnboardingText = Color(0xFF282927)
private val OnboardingGreen = Color(0xFF5DD595)
private val OnboardingMuted = Color(0xFF999999)
private val OnboardingDivider = Color(0x26282927)
private val OnboardingProgressTrack = Color(0xFFEEF4F9)
private const val OnboardingScanLineDurationMillis = 1800

private data class DeviceScanRow(
    val label: String,
    val value: String,
    val complete: Boolean,
    val active: Boolean = false,
)

@Composable
fun OnboardingScanScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {}
    val completedStep = state.completedStep
    val rows = listOf(
        DeviceScanRow(
            label = stringResource(R.string.home_device_model),
            value = state.deviceModel,
            complete = completedStep > 1,
            active = completedStep == 1,
        ),
        DeviceScanRow(
            label = stringResource(R.string.home_system_version),
            value = if (completedStep > 2) state.androidVersion else "--",
            complete = completedStep > 2,
            active = completedStep == 2,
        ),
        DeviceScanRow(
            label = stringResource(R.string.device_screen_size),
            value = if (completedStep > 3) state.screenSize else "--",
            complete = completedStep > 3,
            active = completedStep == 3,
        ),
        DeviceScanRow(
            label = stringResource(R.string.battery_health_status),
            value = if (completedStep > 4) localizedOnboardingDeviceValue(state.batteryHealth) else "--",
            complete = completedStep > 4,
            active = completedStep == 4,
        ),
        DeviceScanRow(
            label = stringResource(R.string.battery_status),
            value = if (completedStep > 5) state.batteryStatusText else "--",
            complete = completedStep > 5,
            active = completedStep == 5,
        ),
        DeviceScanRow(
            label = stringResource(R.string.onboarding_generating_cleanup_plan),
            value = if (completedStep > 6) stringResource(R.string.onboarding_done) else "--",
            complete = completedStep > 6,
            active = completedStep == 6,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingBackground),
    ) {
        if (state.complete) {
            OnboardingResultContent(
                rows = rows,
                storageInfo = state.storageInfo,
                onGetStarted = { onAction(OnboardingAction.GetStarted) },
            )
        } else {
            OnboardingScanningContent(rows = rows)
        }
    }
}

@Composable
private fun OnboardingScanningContent(
    rows: List<DeviceScanRow>,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "deviceScan")
    val scanLineProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = OnboardingScanLineDurationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scanLine",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(62.dp))
        PhoneScanIllustration(
            scanLineProgress = scanLineProgress,
            modifier = Modifier.size(width = 196.dp, height = 192.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.onboarding_checking_device_info),
            color = OnboardingText,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(23.dp))
        DeviceRows(
            rows = rows,
            cardHeight = 376.dp,
        )
    }
}

@Composable
private fun OnboardingResultContent(
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

@Composable
private fun localizedOnboardingDeviceValue(value: String): String =
    when (value) {
        "Good" -> stringResource(R.string.battery_health_good)
        "Cold" -> stringResource(R.string.battery_health_cold)
        "Dead" -> stringResource(R.string.battery_health_dead)
        "Overheat" -> stringResource(R.string.battery_health_overheat)
        "Overvoltage" -> stringResource(R.string.battery_health_overvoltage)
        "Failure" -> stringResource(R.string.battery_health_failure)
        "Unknown" -> stringResource(R.string.device_unknown)
        else -> value
    }

@Composable
private fun StorageCard(storageInfo: StorageInfo) {
    val usedText = compactStorageSize(storageInfo.formattedUsed)
    val totalText = " / ${compactStorageSize(storageInfo.formattedTotal)}"
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
private fun DeviceRows(
    rows: List<DeviceScanRow>,
    cardHeight: androidx.compose.ui.unit.Dp,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            rows.forEachIndexed { index, row ->
                DeviceRow(row = row)
                if (index < rows.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(OnboardingDivider),
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(row: DeviceScanRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(23.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = row.label,
            color = OnboardingText,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = row.value,
                color = OnboardingText,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            StatusBadge(row = row)
        }
    }
}

@Composable
private fun StatusBadge(row: DeviceScanRow) {
    when {
        row.complete -> CompletedBadge()
        row.active -> RotatingRingAnimation(
            modifier = Modifier.size(20.dp),
            ringWidth = 1.75.dp,
            ringColor = OnboardingGreen,
            backgroundColor = Color(0x40999999),
            animationDurationMillis = 1200,
            arcLength = 180f,
        )
        else -> EmptyBadge()
    }
}

@Composable
private fun PhoneScanIllustration(
    scanLineProgress: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.scan_phone_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit,
        )
        ScanCornerFrame(modifier = Modifier.matchParentSize())
        Image(
            painter = painterResource(id = R.drawable.scan_phone),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 96.dp, height = 150.dp),
            contentScale = ContentScale.Fit,
        )
        Image(
            painter = painterResource(id = R.drawable.scan_phone_border),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit,
        )
        Image(
            painter = painterResource(id = R.drawable.scan_phone_line),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = (56 + 90 * scanLineProgress).dp)
                .size(width = 192.dp, height = 18.dp),
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun ScanCornerFrame(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val color = OnboardingGreen
        val stroke = 3.4.dp.toPx()
        val radius = 8.dp.toPx()
        val leg = 13.dp.toPx()
        val left = 10.dp.toPx()
        val right = size.width - 10.dp.toPx()
        val top = 10.dp.toPx()
        val bottom = size.height - 18.dp.toPx()
        val arcSize = Size(radius * 2f, radius * 2f)

        fun drawTopLeft() {
            drawLine(color, Offset(left, top + radius + leg), Offset(left, top + radius), stroke, StrokeCap.Round)
            drawArc(color, 180f, 90f, false, Offset(left, top), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawLine(color, Offset(left + radius, top), Offset(left + radius + leg, top), stroke, StrokeCap.Round)
        }

        fun drawTopRight() {
            drawLine(color, Offset(right, top + radius + leg), Offset(right, top + radius), stroke, StrokeCap.Round)
            drawArc(color, 270f, 90f, false, Offset(right - radius * 2f, top), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawLine(color, Offset(right - radius, top), Offset(right - radius - leg, top), stroke, StrokeCap.Round)
        }

        fun drawBottomLeft() {
            drawLine(color, Offset(left, bottom - radius - leg), Offset(left, bottom - radius), stroke, StrokeCap.Round)
            drawArc(color, 90f, 90f, false, Offset(left, bottom - radius * 2f), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawLine(color, Offset(left + radius, bottom), Offset(left + radius + leg, bottom), stroke, StrokeCap.Round)
        }

        fun drawBottomRight() {
            drawLine(color, Offset(right, bottom - radius - leg), Offset(right, bottom - radius), stroke, StrokeCap.Round)
            drawArc(color, 0f, 90f, false, Offset(right - radius * 2f, bottom - radius * 2f), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawLine(color, Offset(right - radius, bottom), Offset(right - radius - leg, bottom), stroke, StrokeCap.Round)
        }

        drawTopLeft()
        drawTopRight()
        drawBottomLeft()
        drawBottomRight()
    }
}

@Composable
private fun CompletedBadge() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(OnboardingGreen),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            drawLine(
                color = Color.White,
                start = Offset(5.dp.toPx(), 10.dp.toPx()),
                end = Offset(8.5.dp.toPx(), 13.5.dp.toPx()),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color.White,
                start = Offset(8.5.dp.toPx(), 13.5.dp.toPx()),
                end = Offset(15.dp.toPx(), 6.5.dp.toPx()),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun EmptyBadge() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .border(width = 1.75.dp, color = Color(0x40999999), shape = CircleShape),
    )
}

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
