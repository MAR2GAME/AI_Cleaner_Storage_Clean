package com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderBottomPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderTopPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXIconButtonSize

private val ToolboxScanText = Color(0xFF282927)

@Composable
internal fun ToolboxScanningContent(
    @DrawableRes centerIconRes: Int,
    captionText: String,
    modifier: Modifier = Modifier,
    centerIconSize: Dp = 74.dp,
) {
    val topBarHeight =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() +
            CleanXHeaderTopPadding +
            CleanXIconButtonSize +
            CleanXHeaderBottomPadding

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .offset(y = -topBarHeight),
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
            ToolboxScanCenterIcon(centerIconRes, centerIconSize)
        }
        Text(
            text = captionText,
            color = ToolboxScanText,
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

@Composable
private fun ToolboxScanCenterIcon(
    @DrawableRes centerIconRes: Int,
    centerIconSize: Dp,
) {
    Image(
        painter = painterResource(centerIconRes),
        contentDescription = null,
        modifier = Modifier.size(centerIconSize)
            .clip(RoundedCornerShape(10.dp)),
    )
}
