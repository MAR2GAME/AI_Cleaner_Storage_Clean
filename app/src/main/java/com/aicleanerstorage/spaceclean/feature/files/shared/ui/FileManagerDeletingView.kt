package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage
import kotlinx.coroutines.launch

@Composable
internal fun FileManagerDeletingView(
    fallbackText: String? = null,
    stage: CleanXDeleteCompleteStage = CleanXDeleteCompleteStage.Deleting,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (stage == CleanXDeleteCompleteStage.Complete) {
            FileManagerCompleteAnimation(
                text = fallbackText ?: stringResource(R.string.file_cleanup_completed),
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
                    painter = painterResource(id = R.drawable.junk_cleaning_trash_figma),
                    contentDescription = null,
                    modifier = Modifier.size(78.dp),
                )
            }
        }
    }
}

@Composable
private fun FileManagerCompleteAnimation(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        FileManagerCompleteMark(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 174.dp),
        )
        Text(
            text = text,
            color = FileManagerPhaseTextColor,
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

@Composable
private fun FileManagerCompleteMark(modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0.35f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing),
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            )
        }
    }

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_file_manager_complete),
            contentDescription = null,
            modifier =
                Modifier
                    .size(122.dp)
                    .alpha(alpha.value)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        transformOrigin = TransformOrigin.Center
                    },
        )
    }
}
