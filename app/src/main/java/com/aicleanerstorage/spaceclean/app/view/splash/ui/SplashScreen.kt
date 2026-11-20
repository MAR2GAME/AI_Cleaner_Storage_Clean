package com.aicleanerstorage.spaceclean.app.view.splash.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashAction
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashUiState
import com.aicleanerstorage.spaceclean.app.view.splash.ui.part.SplashContent

@Composable
fun SplashScreen(
    state: SplashUiState,
    onAction: (SplashAction) -> Unit,
    onOpenLegalDocument: (SplashLegalDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {}

    val progress = rememberSplashProgress(state, onAction)
    SplashContent(
        progress = progress.value,
        activeStage = state.progressStage,
        paused = state.paused,
        onOpenLegalDocument = onOpenLegalDocument,
        modifier = modifier,
    )
}
