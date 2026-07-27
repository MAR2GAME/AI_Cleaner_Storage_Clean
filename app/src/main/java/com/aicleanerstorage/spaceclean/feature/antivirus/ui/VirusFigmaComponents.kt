package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXTopAppBar
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanMode

internal val VirusFigmaBackground = Color(0xFFF4F4F4)
internal val VirusFigmaText = Color(0xFF282927)
private val VirusFigmaGreen = Color(0xFF5DD595)
private val VirusFigmaLime = Color(0xFFD0FD91)
private val VirusFigmaInactive = Color(0xFF999999)
private val VirusFigmaDanger = Color(0xFFEE4D52)

@Composable
internal fun VirusFigmaPage(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(VirusFigmaBackground),
    ) {
        CleanXTopAppBar(
            title = stringResource(R.string.anti_virus),
            onBack = onBack,
        )

        content()
    }
}

@Composable
internal fun BoxScope.VirusFigmaSpiral(
    content: @Composable BoxScope.() -> Unit,
) {
    CleanSpiralAnimation(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = 103.dp),
        containerSize = 268.dp,
        centerSize = 200.dp,
        spiralResId = R.drawable.junk_scan_spiral_figma,
        content = content,
    )
}

@Composable
internal fun VirusFigmaFeatureCard(
    modifier: Modifier = Modifier,
) {
    val features =
        listOf(
            R.mipmap.ic_virus to R.string.virus,
            R.mipmap.ic_malware to R.string.malware,
            R.mipmap.ic_privacy to R.string.privacy,
        )

    Row(
        modifier =
            modifier
                .width(343.dp)
                .height(87.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        features.forEachIndexed { index, feature ->
            Box(
                modifier =
                    Modifier
                        .width(if (index == 1) 70.dp else 71.dp)
                        .height(55.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(feature.first),
                        contentDescription = null,
                        tint = VirusFigmaText,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(feature.second),
                        color = VirusFigmaText,
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                    )
                }
            }

            if (index < features.lastIndex) {
                Box(
                    modifier =
                        Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(VirusFigmaText.copy(alpha = 0.15f)),
                )
            }
        }
    }
}

@Composable
internal fun VirusFigmaScanButton(
    text: String,
    onClick: () -> Unit,
    filled: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(26.dp)
    val buttonModifier =
        modifier
            .width(343.dp)
            .height(52.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .clip(shape)

    Box(
        modifier =
            if (filled) {
                buttonModifier
                    .background(Brush.horizontalGradient(listOf(VirusFigmaLime, VirusFigmaGreen)))
                    .clickable(enabled = enabled, onClick = onClick)
            } else {
                buttonModifier
                    .border(1.5.dp, VirusFigmaInactive, shape)
                    .clickable(enabled = enabled, onClick = onClick)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (filled) VirusFigmaText else VirusFigmaInactive,
            fontSize = 20.sp,
            lineHeight = 23.sp,
            fontWeight = if (filled) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun VirusFigmaProgressTrack(
    mode: VirusScanMode,
    progress: Float,
    hasAdbRisk: Boolean,
    appThreatCount: Int,
    fileThreatCount: Int,
    modifier: Modifier = Modifier,
) {
    val positions =
        if (mode == VirusScanMode.Deep) {
            listOf(0.dp, 87.dp, 174.dp, 261.dp)
        } else {
            listOf(0.dp, 134.dp, 259.dp)
        }
    val icons =
        if (mode == VirusScanMode.Deep) {
            listOf(R.mipmap.ic_lock_small, R.mipmap.ic_virus_small, R.mipmap.ic_malware_small, R.mipmap.ic_file_small)
        } else {
            listOf(R.mipmap.ic_lock_small, R.mipmap.ic_virus_small, R.mipmap.ic_malware_small)
        }
    val badges =
        if (mode == VirusScanMode.Deep) {
            listOf(if (hasAdbRisk) 1 else 0, 0, appThreatCount, fileThreatCount)
        } else {
            listOf(if (hasAdbRisk) 1 else 0, 0, appThreatCount)
        }
    val clampedProgress = progress.coerceIn(0f, 1f)
    val visualWidth = if (mode == VirusScanMode.Deep) 317.dp else 315.dp
    val fillEnd = visualWidth.value * clampedProgress

    Box(
        modifier =
            modifier
                .width(317.dp)
                .height(56.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineLeft = 4.dp.toPx()
            val lineTop = 22.dp.toPx()
            val lineWidth = 311.dp.toPx()
            val lineHeight = 12.dp.toPx()
            val radius = 28.dp.toPx()

            drawRoundRect(
                color = VirusFigmaInactive,
                topLeft = androidx.compose.ui.geometry.Offset(lineLeft, lineTop),
                size = androidx.compose.ui.geometry.Size(lineWidth, lineHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
            )
            positions.forEach { x ->
                drawCircle(
                    color = VirusFigmaInactive,
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(x.toPx() + radius, radius),
                )
            }

            clipRect(right = visualWidth.toPx() * clampedProgress) {
                drawRoundRect(
                    color = VirusFigmaGreen,
                    topLeft = androidx.compose.ui.geometry.Offset(lineLeft, lineTop),
                    size = androidx.compose.ui.geometry.Size(lineWidth, lineHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                )
                positions.forEach { x ->
                    drawCircle(
                        color = VirusFigmaGreen,
                        radius = radius,
                        center = androidx.compose.ui.geometry.Offset(x.toPx() + radius, radius),
                    )
                }
            }
        }

        positions.forEachIndexed { index, x ->
            val iconSize = if (mode == VirusScanMode.Deep && index == icons.lastIndex) 33.dp else 38.dp
            Image(
                painter = painterResource(icons[index]),
                contentDescription = null,
                modifier =
                    Modifier
                        .offset(
                            x = x + (56.dp - iconSize) / 2,
                            y = (56.dp - iconSize) / 2,
                        )
                        .size(iconSize),
                contentScale = ContentScale.Fit,
            )

            val badgeCount = badges[index]
            val nodeCompleted = fillEnd >= x.value + 56f
            if (nodeCompleted && badgeCount > 0) {
                Box(
                    modifier =
                        Modifier
                            .offset(x = x + 42.5.dp)
                            .size(13.5.dp)
                            .clip(CircleShape)
                            .background(VirusFigmaDanger)
                            .border(0.7.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 8.sp,
                        lineHeight = 10.sp,
                        modifier = Modifier.graphicsLayer(rotationZ = 15.09f),
                    )
                }
            }
        }
    }
}
