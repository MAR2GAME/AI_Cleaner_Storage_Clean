package com.aicleanerstorage.spaceclean.app.view.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingAction
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingUiState
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.OnboardingResultContent
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.OnboardingScanningContent
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.OnboardingSkipButton
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.onboardingDeviceRows
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableStatusBarsPadding

@Composable
fun OnboardingScanScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {}
    val rows = onboardingDeviceRows(state)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(OnboardingBackground)
                .stableStatusBarsPadding()
                .stableNavigationBarsPadding(),
    ) {
        if (state.complete) {
            OnboardingResultContent(
                rows = rows,
                storageInfo = state.storageInfo,
                onGetStarted = { onAction(OnboardingAction.GetStartedClicked) },
            )
            OnboardingSkipButton(
                onClick = { onAction(OnboardingAction.SkipClicked) },
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp),
            )
        } else {
            OnboardingScanningContent(rows = rows)
        }
    }
}

private val OnboardingBackground = Color(0xFFF4F4F4)
