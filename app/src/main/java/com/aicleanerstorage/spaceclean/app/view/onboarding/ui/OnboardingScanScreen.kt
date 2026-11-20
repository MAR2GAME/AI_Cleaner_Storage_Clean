package com.aicleanerstorage.spaceclean.app.view.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingAction
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingUiState
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.OnboardingResultContent
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.OnboardingScanningContent
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part.onboardingDeviceRows

@Composable
fun OnboardingScanScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {}
    val rows = onboardingDeviceRows(state)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingBackground),
    ) {
        if (state.complete) {
            OnboardingResultContent(
                rows = rows,
                storageInfo = state.storageInfo,
                onGetStarted = { onAction(OnboardingAction.GetStartedClicked) },
            )
        } else {
            OnboardingScanningContent(rows = rows)
        }
    }
}

private val OnboardingBackground = Color(0xFFF4F4F4)
