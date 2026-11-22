package com.aicleanerstorage.spaceclean.base.designsystem.component

import android.os.SystemClock
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

val CleanXBackground = Color(0xFFF7FAFD)
val CleanXText = Color(0xFF2D3748)
val CleanXMutedText = Color(0xFF8190A5)
val CleanXBlue = Color(0xFF5DD595)
val CleanXDivider = Color(0xFFE2EAF3)

val CleanXPagePadding = 16.dp
val CleanXCompactPadding = 8.dp
val CleanXButtonHorizontalPadding = 18.dp
val CleanXButtonHeight = 50.dp
val CleanXCompactButtonHeight = 36.dp
val CleanXIconButtonSize = 40.dp
val CleanXHeaderIconSize = 28.dp
val CleanXHeaderTopPadding = 12.dp
val CleanXHeaderBottomPadding = 14.dp
val CleanXTextTitle = 20.sp
val CleanXTextBody = 16.sp
val CleanXTextCaption = 14.sp
val CleanXTextTiny = 12.sp
val CleanXLineTitle = 27.sp
val CleanXLineSubtitle = 22.sp
val CleanXLineBody = 20.sp
val CleanXLineCaption = 18.sp
val CleanXPillShape = RoundedCornerShape(50)
val CleanXTileShape = RoundedCornerShape(12.dp)

fun Modifier.cleanXDebouncedClick(
    enabled: Boolean = true,
    debounceMillis: Long = 500L,
    onClick: () -> Unit,
): Modifier =
    composed {
        val lastClickTime = remember { LongArray(1) }
        clickable(enabled = enabled) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime[0] >= debounceMillis) {
                lastClickTime[0] = now
                onClick()
            }
        }
    }

fun Modifier.cleanXPressable(
    enabled: Boolean = true,
    pressedAlpha: Float = 0.9f,
    pressedScale: Float = 1f,
    onClick: () -> Unit,
): Modifier =
    composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState().value
        val alpha =
            animateFloatAsState(
                targetValue = if (enabled && isPressed) pressedAlpha else 1f,
                animationSpec = tween(durationMillis = 90),
                label = "cleanXPressAlpha",
            ).value
        val scale =
            animateFloatAsState(
                targetValue = if (enabled && isPressed) pressedScale else 1f,
                animationSpec = tween(durationMillis = 90),
                label = "cleanXPressScale",
            ).value

        graphicsLayer(
            alpha = alpha,
            scaleX = scale,
            scaleY = scale,
        ).clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
    }

fun Modifier.cleanXPressFeedback(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    pressedAlpha: Float = 0.9f,
    pressedScale: Float = 1f,
): Modifier =
    composed {
        val isPressed = interactionSource.collectIsPressedAsState().value
        val alpha =
            animateFloatAsState(
                targetValue = if (enabled && isPressed) pressedAlpha else 1f,
                animationSpec = tween(durationMillis = 90),
                label = "cleanXButtonPressAlpha",
            ).value
        val scale =
            animateFloatAsState(
                targetValue = if (enabled && isPressed) pressedScale else 1f,
                animationSpec = tween(durationMillis = 90),
                label = "cleanXButtonPressScale",
            ).value

        graphicsLayer(
            alpha = alpha,
            scaleX = scale,
            scaleY = scale,
        )
    }

@Composable
fun CleanXPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = CleanXButtonHeight,
    cornerRadius: Dp = 50.dp,
    fontSize: TextUnit = CleanXTextTitle,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .cleanXPressFeedback(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    pressedAlpha = 0.88f,
                    pressedScale = 0.98f,
                ),
        shape = RoundedCornerShape(cornerRadius),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = CleanXBlue,
                contentColor = Color.White,
                disabledContainerColor = CleanXBlue.copy(alpha = 0.45f),
                disabledContentColor = Color.White,
            ),
        contentPadding = PaddingValues(horizontal = CleanXButtonHorizontalPadding),
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            lineHeight = CleanXLineSubtitle,
            fontWeight = FontWeight.W500,
        )
    }
}

@Composable
fun CleanXBottomActionBar(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = CleanXBackground,
    buttonModifier: Modifier = Modifier,
    buttonCornerRadius: Dp = 50.dp,
    buttonFontSize: TextUnit = CleanXTextTitle,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .stableNavigationBarsPadding()
                .padding(horizontal = CleanXPagePadding, vertical = CleanXPagePadding),
    ) {
        CleanXPrimaryButton(
            text = text,
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            cornerRadius = buttonCornerRadius,
            fontSize = buttonFontSize,
        )
    }
}

@Composable
fun CommonResultCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 45.dp,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_result_ok),
            contentDescription = null,
            modifier = Modifier.size(size),
        )
    }
}
