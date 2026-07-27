package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXSpiralCleaningContent

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
            CleanXSpiralCleaningContent(
                captionText = stringResource(R.string.scan_loading_fallback),
            )
        }
    }
}

@Composable
private fun JunkCleanCompleteMark(modifier: Modifier = Modifier) {
    var animationStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.35f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing,
        ),
        label = "junkCleanCompleteScale",
    )

    LaunchedEffect(Unit) {
        animationStarted = true
    }

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
