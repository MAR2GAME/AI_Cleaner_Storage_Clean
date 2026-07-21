package com.aicleanerstorage.spaceclean.common.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedProgressBar(
    progress: Float,
    width: Dp = 320.dp,
    height: Dp = 10.dp,
    trackColor: Color = Color(0x59FFFFFF),
    fillColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier
                .size(width = width, height = height),
    ) {
        val cornerRadiusPx = size.height / 2
        val cornerRadius = CornerRadius(cornerRadiusPx)

        drawRoundRect(
            color = trackColor,
            cornerRadius = cornerRadius,
        )

        if (progress > 0f) {
            val fillWidth = size.width * progress.coerceIn(0f, 1f)
            drawRoundRect(
                color = fillColor,
                topLeft = Offset.Zero,
                size = Size(fillWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}
