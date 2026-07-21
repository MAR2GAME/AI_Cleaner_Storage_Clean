package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage

private val JunkCompleteGreen = Color(0xFF5DD595)
private val JunkCompleteLime = Color(0xFFD0FD91)

@Composable
internal fun JunkCleaningView(
    stage: CleanXDeleteCompleteStage = CleanXDeleteCompleteStage.Deleting,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (stage == CleanXDeleteCompleteStage.Complete) {
            JunkCleanCompleteMark(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 174.dp),
            )
        } else {
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
                text = stringResource(R.string.scan_loading_fallback),
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
}

@Composable
private fun JunkCleanCompleteMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiShard(
            color = Color(0xFF39ECA4),
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = 28.dp)
                    .rotate(18f),
        )
        ConfettiShard(
            color = Color(0xFF857DF5),
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 4.dp)
                    .rotate(-29f),
        )
        ConfettiShard(
            color = Color(0xFF24E4D6),
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 12.dp, y = (-28).dp)
                    .rotate(-22f)
                    .scale(0.72f),
        )
        Box(
            modifier =
                Modifier
                    .size(198.dp)
                    .background(Color(0x4D5DD595), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(145.dp)
                        .background(
                            Brush.linearGradient(listOf(JunkCompleteLime, JunkCompleteGreen)),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp),
                )
            }
        }
    }
}

@Composable
private fun ConfettiShard(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 28.dp, height = 14.dp)
                .background(color, CircleShape),
    )
}
