package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode

@Composable
internal fun VirusScanProgressBar(
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
                topLeft =
                    androidx.compose.ui.geometry
                        .Offset(lineLeft, lineTop),
                size =
                    androidx.compose.ui.geometry
                        .Size(lineWidth, lineHeight),
                cornerRadius =
                    androidx.compose.ui.geometry
                        .CornerRadius(6.dp.toPx()),
            )
            positions.forEach { x ->
                drawCircle(
                    color = VirusFigmaInactive,
                    radius = radius,
                    center =
                        androidx.compose.ui.geometry
                            .Offset(x.toPx() + radius, radius),
                )
            }

            clipRect(right = visualWidth.toPx() * clampedProgress) {
                drawRoundRect(
                    color = VirusFigmaGreen,
                    topLeft =
                        androidx.compose.ui.geometry
                            .Offset(lineLeft, lineTop),
                    size =
                        androidx.compose.ui.geometry
                            .Size(lineWidth, lineHeight),
                    cornerRadius =
                        androidx.compose.ui.geometry
                            .CornerRadius(6.dp.toPx()),
                )
                positions.forEach { x ->
                    drawCircle(
                        color = VirusFigmaGreen,
                        radius = radius,
                        center =
                            androidx.compose.ui.geometry
                                .Offset(x.toPx() + radius, radius),
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
                        ).size(iconSize),
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

private val VirusFigmaDanger = Color(0xFFEE4D52)
