package com.aicleanerstorage.spaceclean.common.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerPrimaryGradient
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXPressFeedback
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding

@Composable
fun CleanXGradientBottomActionBar(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    height: Dp = 52.dp,
    cornerRadius: Dp = 26.dp,
    fontSize: TextUnit = 20.sp,
    contentColor: Color = FileManagerTextColor,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .stableNavigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(FileManagerPrimaryGradient)
                .graphicsLayer(alpha = if (enabled) 1f else 0.45f)
                .cleanXPressFeedback(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    pressedAlpha = 0.88f,
                    pressedScale = 0.98f,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = fontSize,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
