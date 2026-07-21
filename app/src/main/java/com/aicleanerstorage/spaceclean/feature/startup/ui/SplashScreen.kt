package com.aicleanerstorage.spaceclean.feature.startup.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.theme.StorageCleanAppTheme
import com.aicleanerstorage.spaceclean.feature.startup.SplashAction
import com.aicleanerstorage.spaceclean.feature.startup.SplashProgressStage
import com.aicleanerstorage.spaceclean.feature.startup.SplashUiState
import com.aicleanerstorage.spaceclean.feature.startup.targetProgress
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    state: SplashUiState,
    onAction: (SplashAction) -> Unit,
    onOpenLegalDocument: (SplashLegalDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {}

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
            latestOnAction(SplashAction.VisualReady)
        }
        if (state.progressStage != SplashProgressStage.Finishing || !state.finishRequested) {
            return@LaunchedEffect
        }
        delay(STARTUP_FINISH_HOLD_MS)
        visualFinished = true
        latestOnAction(SplashAction.VisualFinished)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val widthScale = maxWidth.value / FIGMA_WIDTH_DP
        val heightScale = maxHeight.value / FIGMA_HEIGHT_DP
        val elementScale = minOf(widthScale, heightScale)
        val textScale = elementScale.coerceIn(0.85f, 1.15f)

        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = maxHeight * (FIGMA_LOGO_TOP_DP / FIGMA_HEIGHT_DP))
                    .size(FIGMA_LOGO_SIZE_DP.dp * elementScale)
                    .clip(RoundedCornerShape(FIGMA_LOGO_CORNER_DP.dp * elementScale)),
            contentScale = ContentScale.FillBounds,
        )

        SplashStageProgressBar(
            progress = progressAnim.value,
            activeStage = state.progressStage,
            paused = state.paused,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = maxHeight * (FIGMA_PROGRESS_TOP_DP / FIGMA_HEIGHT_DP))
                    .width(maxWidth * (FIGMA_PROGRESS_WIDTH_DP / FIGMA_WIDTH_DP))
                    .height(FIGMA_PROGRESS_HEIGHT_DP.dp * elementScale),
        )

        val termsTag = "terms"
        val privacyTag = "privacy"
        val legalText =
            buildAnnotatedString {
                append(stringResource(R.string.splash_accept_prefix).trimEnd())
                append(" ")
                pushStringAnnotation(tag = termsTag, annotation = termsTag)
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.settings_terms_of_service))
                }
                pop()
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(" | ")
                }
                pushStringAnnotation(tag = privacyTag, annotation = privacyTag)
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.settings_privacy_policy))
                }
                pop()
            }

        @Suppress("DEPRECATION")
        ClickableText(
            text = legalText,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = maxHeight * (FIGMA_LEGAL_TOP_DP / FIGMA_HEIGHT_DP))
                    .width(maxWidth * (FIGMA_LEGAL_WIDTH_DP / FIGMA_WIDTH_DP)),
            style =
                TextStyle(
                    color = Color.White,
                    fontFamily = SplashFontFamily,
                    fontSize = 18.sp * textScale,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp * textScale,
                    textAlign = TextAlign.Center,
                ),
            onClick = { offset ->
                when {
                    legalText.getStringAnnotations(termsTag, offset, offset).isNotEmpty() ->
                        onOpenLegalDocument(SplashLegalDocument.Terms)
                    legalText.getStringAnnotations(privacyTag, offset, offset).isNotEmpty() ->
                        onOpenLegalDocument(SplashLegalDocument.Privacy)
                }
            },
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PreviewSplashScreen() {
    StorageCleanAppTheme {
        SplashScreen(
            state = SplashUiState(),
            onAction = {},
            onOpenLegalDocument = {},
        )
    }
}

enum class SplashLegalDocument {
    Terms,
    Privacy,
}

private val SplashFontFamily =
    FontFamily(
        Font(R.font.wix_madefor_text_variable, FontWeight.Normal),
        Font(R.font.wix_madefor_text_variable, FontWeight.SemiBold),
    )

private const val FIGMA_WIDTH_DP = 375f
private const val FIGMA_HEIGHT_DP = 812f
private const val FIGMA_LOGO_TOP_DP = 235f
private const val FIGMA_LOGO_SIZE_DP = 126f
private const val FIGMA_LOGO_CORNER_DP = 30f
private const val FIGMA_PROGRESS_TOP_DP = 603f
private const val FIGMA_PROGRESS_WIDTH_DP = 311f
private const val FIGMA_PROGRESS_HEIGHT_DP = 12f
private const val FIGMA_LEGAL_TOP_DP = 655f
private const val FIGMA_LEGAL_WIDTH_DP = 327f
private const val STARTUP_FINISH_HOLD_MS = 300L

private val SplashProgressStage.startProgress: Float
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 0f
            SplashProgressStage.Finishing -> 0.25f
            SplashProgressStage.Completed -> 1f
        }

private val SplashProgressStage.durationMillis: Int
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 700
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
