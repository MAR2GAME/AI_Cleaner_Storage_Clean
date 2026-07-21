package com.aicleanerstorage.spaceclean.feature.startup.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.feature.startup.SplashProgressStage

@Composable
internal fun SplashStageProgressBar(
    progress: Float,
    activeStage: SplashProgressStage,
    paused: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.size(width = 311.dp, height = 12.dp),
    ) {
        val radius = size.height / 2
        val cornerRadius = CornerRadius(radius)
        val fillWidth = size.width * progress.coerceIn(0f, 1f)

        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            cornerRadius = cornerRadius,
        )
        if (fillWidth > 0f) {
            drawRoundRect(
                color = Color.White,
                size = Size(fillWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}
