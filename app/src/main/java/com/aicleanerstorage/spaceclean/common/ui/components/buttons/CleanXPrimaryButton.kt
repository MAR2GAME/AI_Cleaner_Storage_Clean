package com.aicleanerstorage.spaceclean.common.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXPressFeedback
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonHeight
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonHorizontalPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonShape
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonVerticalPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXLineSubtitle
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXTextTitle
import androidx.compose.ui.graphics.Brush

@Composable
fun CleanXPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(CleanXButtonHeight)
            .cleanXPressFeedback(
                interactionSource = interactionSource,
                enabled = enabled,
                pressedAlpha = 0.88f,
                pressedScale = 0.98f,
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD0FD91).copy(alpha = if (enabled) 1f else 0.45f),
                        Color(0xFF5DD595).copy(alpha = if (enabled) 1f else 0.45f)
                    )
                ),
                shape = CleanXButtonShape,
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(
            horizontal = CleanXButtonHorizontalPadding,
            vertical = CleanXButtonVerticalPadding
        )
    ) {
        Text(
            text = text,
            fontSize = CleanXTextTitle,
            lineHeight = CleanXLineSubtitle,
            fontWeight = FontWeight.W500
        )
    }
}
