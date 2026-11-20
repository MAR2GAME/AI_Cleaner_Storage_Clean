package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun VirusScanActionBar(
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

internal val VirusFigmaGreen = Color(0xFF5DD595)

private val VirusFigmaLime = Color(0xFFD0FD91)

internal val VirusFigmaInactive = Color(0xFF999999)
