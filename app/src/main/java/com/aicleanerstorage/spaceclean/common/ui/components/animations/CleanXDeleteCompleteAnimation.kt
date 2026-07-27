package com.aicleanerstorage.spaceclean.common.ui.components.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXBackground
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderBottomPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXHeaderTopPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXIconButtonSize

enum class CleanXDeleteCompleteStage {
    Deleting,
    Complete,
}

@Composable
fun CleanXDeleteCompleteAnimation(
    stage: CleanXDeleteCompleteStage,
    modifier: Modifier = Modifier,
    cleaningText: String,
    completeText: String? = null,
    backgroundColor: Color = CleanXBackground,
    drawBackground: Boolean = true,
) {
    val isComplete = stage == CleanXDeleteCompleteStage.Complete
    val backgroundModifier =
        if (drawBackground) {
            Modifier.background(backgroundColor)
        } else {
            Modifier
        }
    val topBarHeight =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() +
            CleanXHeaderTopPadding +
            CleanXIconButtonSize +
            CleanXHeaderBottomPadding

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .then(backgroundModifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (isComplete) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset(y = -topBarHeight),
                contentAlignment = Alignment.TopCenter,
            ) {
                CleanXDeleteCompleteCheckAnimation(
                    isVisible = true,
                    modifier = Modifier.padding(top = 174.dp),
                )
                completeText?.let { text ->
                    Text(
                        text = text,
                        color = Color(0xFF282927),
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 479.dp),
                    )
                }
            }
        } else {
            CleanXSpiralCleaningContent(
                captionText = cleaningText,
                contentOffsetY = -topBarHeight,
            )
        }
    }
}

@Composable
private fun CleanXDeleteCompleteCheckAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    var animationStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.35f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing,
        ),
        label = "whatsAppCleanCompleteScale",
    )

    LaunchedEffect(isVisible) {
        animationStarted = isVisible
    }

    if (isVisible) {
        Image(
            painter = painterResource(R.drawable.ic_clean_ok),
            contentDescription = null,
            modifier =
                modifier
                    .size(250.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
        )
    }
}
