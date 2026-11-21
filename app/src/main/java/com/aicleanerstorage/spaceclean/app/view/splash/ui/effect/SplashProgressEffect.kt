package com.aicleanerstorage.spaceclean.app.view.splash.ui.effect

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
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashStage
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashUiState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun rememberSplashProgressEffect(
    state: SplashUiState,
    onAction: (SplashAction) -> Unit,
): Animatable<Float, AnimationVector1D> {
    val progressAnim = remember { Animatable(0f) }
    var visualReadyDispatched by remember { mutableStateOf(false) }
    var visualFinished by remember { mutableStateOf(false) }
    val latestOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(
        state.paused,
        state.finishRequested,
        state.stage,
        visualReadyDispatched,
        visualFinished,
    ) {
        if (visualFinished) return@LaunchedEffect
        val targetProgress = state.stage.targetProgress
        if (state.paused) return@LaunchedEffect
        if (progressAnim.value < targetProgress) {
            progressAnim.animateTo(
                targetValue = targetProgress,
                animationSpec =
                    tween(
                        durationMillis =
                            remainingDurationMillis(
                                current = progressAnim.value,
                                start = state.stage.startProgress,
                                target = targetProgress,
                                durationMillis = state.stage.durationMillis,
                            ),
                        easing = FastOutSlowInEasing,
                    ),
            )
        }
        if (state.stage == SplashStage.Preparing && !visualReadyDispatched) {
            visualReadyDispatched = true
            latestOnAction(SplashAction.VisualPreparationCompleted)
        }
        if (state.stage != SplashStage.Finishing || !state.finishRequested) {
            return@LaunchedEffect
        }
        delay(STARTUP_FINISH_HOLD_MS)
        visualFinished = true
        latestOnAction(SplashAction.VisualFinished)
    }
    return progressAnim
}

private const val STARTUP_FINISH_HOLD_MS = 300L

private val SplashStage.targetProgress: Float
    get() =
        when (this) {
            SplashStage.Preparing -> 0.25f
            SplashStage.Finishing,
            SplashStage.Completed,
            -> 1f
        }

private val SplashStage.startProgress: Float
    get() =
        when (this) {
            SplashStage.Preparing -> 0f
            SplashStage.Finishing -> 0.25f
            SplashStage.Completed -> 1f
        }

private val SplashStage.durationMillis: Int
    get() =
        when (this) {
            SplashStage.Preparing -> 700
            SplashStage.Finishing -> 1_500
            SplashStage.Completed -> 1
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
