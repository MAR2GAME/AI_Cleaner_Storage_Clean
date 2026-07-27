package com.aicleanerstorage.spaceclean.common.ui.components.animations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun CleanXSpiralCleaningContent(
    captionText: String,
    modifier: Modifier = Modifier,
    contentOffsetY: Dp = 0.dp,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .offset(y = contentOffsetY),
    ) {
        CleanSpiralAnimation(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 163.dp),
            containerSize = 268.dp,
            centerSize = 200.dp,
            spiralResId = R.drawable.junk_scan_spiral_figma,
            animationDurationMillis = 2000,
        ) {
            Image(
                painter = painterResource(R.drawable.junk_cleaning_trash_figma),
                contentDescription = null,
                modifier = Modifier.size(78.dp),
            )
        }
        Text(
            text = captionText,
            color = Color(0xFF282927),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 458.dp),
        )
    }
}
