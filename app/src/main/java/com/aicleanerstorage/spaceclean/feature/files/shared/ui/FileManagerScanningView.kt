package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderBottomPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderTopPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXIconButtonSize

@Composable
internal fun FileManagerScanningView(text: String = "Scanning...") {
    val topBarHeight =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() +
            CleanXHeaderTopPadding +
            CleanXIconButtonSize +
            CleanXHeaderBottomPadding
    Box(
        modifier =
            Modifier
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
            Image(
                painter = painterResource(id = R.drawable.file_yellow),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
            )
        }
//        Text(
//            text = text,
//            color = FileManagerPhaseTextColor,
//            fontSize = 20.sp,
//            lineHeight = 24.sp,
//            fontWeight = FontWeight.Normal,
//            textAlign = TextAlign.Center,
//            modifier =
//                Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = 458.dp),
//        )
    }
}
