package com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPillShape
import com.aicleanerstorage.spaceclean.base.designsystem.component.cleanXPressFeedback

/**
 * 引导结果页右上角的「跳过」。
 *
 * 外观沿用同页白色卡片 + 深灰次要文字的视觉；48dp 的可点区域包住 32dp 的胶囊，
 * 这样既不破排版又满足最小触控尺寸。浮在内容上方而不是占一行，避免把结果卡片往下顶。
 */
@Composable
internal fun OnboardingSkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .height(32.dp)
                    .clip(CleanXPillShape)
                    .background(Color.White)
                    .cleanXPressFeedback(
                        interactionSource = interactionSource,
                        pressedAlpha = 0.92f,
                        pressedScale = 0.96f,
                    ).padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = OnboardingSkipText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private val OnboardingSkipText = Color(0xFF666666)
