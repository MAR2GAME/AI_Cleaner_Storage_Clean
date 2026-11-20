package com.aicleanerstorage.spaceclean.app.view.splash.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashAction
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashProgressStage
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashUiState
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.targetProgress
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
internal fun rememberSplashProgress(state: SplashUiState, onAction: (SplashAction) -> Unit): Animatable<Float, AnimationVector1D> {
    val progressAnim = remember { Animatable(0f) }
    var visualReadyDispatched by remember { mutableStateOf(false) }
    var visualFinished by remember { mutableStateOf(false) }
    val latestOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(
        state.paused,
        state.finishRequested,
        state.progressStage,
        visualReadyDispatched,
        visualFinished,
    ) {
        if (visualFinished) return@LaunchedEffect
        val targetProgress = state.progressStage.targetProgress
        if (state.paused) return@LaunchedEffect
        if (progressAnim.value < targetProgress) {
            progressAnim.animateTo(
                targetValue = targetProgress,
                animationSpec =
                    tween(
                        durationMillis =
                            remainingDurationMillis(
                                current = progressAnim.value,
                                start = state.progressStage.startProgress,
                                target = targetProgress,
                                durationMillis = state.progressStage.durationMillis,
                            ),
                        easing = FastOutSlowInEasing,
                    ),
            )
        }
        if (state.progressStage == SplashProgressStage.Preparing && !visualReadyDispatched) {
            visualReadyDispatched = true
            latestOnAction(SplashAction.VisualPreparationCompleted)
        }
        if (state.progressStage != SplashProgressStage.Finishing || !state.finishRequested) {
            return@LaunchedEffect
        }
        delay(STARTUP_FINISH_HOLD_MS)
        visualFinished = true
        latestOnAction(SplashAction.VisualFinished)
    }
    return progressAnim
}

private const val STARTUP_FINISH_HOLD_MS = 300L

private val SplashProgressStage.startProgress: Float
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 0f
            SplashProgressStage.RequestingAd -> 0.25f
            SplashProgressStage.ShowingAd,
            SplashProgressStage.Finishing -> 0.25f
            SplashProgressStage.Completed -> 1f
        }

private val SplashProgressStage.durationMillis: Int
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 700
            SplashProgressStage.RequestingAd,
            SplashProgressStage.ShowingAd,
            -> 1
            SplashProgressStage.Finishing -> 1_500
            SplashProgressStage.Completed -> 1
        }

private fun remainingDurationMillis(
    current: Float,
    start: Float,
    target: Float,
    durationMillis: Int,
): Int {
    val totalDistance = target - start
    if (totalDistance <= 0f) return durationMillis
    val remainingRatio = ((target - current) / totalDistance).coerceIn(0f, 1f)
    return (durationMillis * remainingRatio).roundToInt().coerceAtLeast(1)
}
