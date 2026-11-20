package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

data class SplashUiState(
    val stage: SplashStage = SplashStage.Preparing,
    val visualReady: Boolean = false,
    val pauseReasons: Set<SplashPauseReason> = emptySet(),
) {
    val paused: Boolean get() = pauseReasons.isNotEmpty()
    val finishRequested: Boolean get() = stage == SplashStage.Finishing
    val progressStage: SplashProgressStage get() = when (stage) {
        SplashStage.Preparing -> SplashProgressStage.Preparing
        SplashStage.Finishing -> SplashProgressStage.Finishing
        SplashStage.Completed -> SplashProgressStage.Completed
    }
}

data class SplashTransition(
    val state: SplashUiState,
    val events: List<SplashEvent> = emptyList(),
)

fun reduceSplashState(
    state: SplashUiState,
    action: SplashAction,
    normalDestination: AppDestination,
): SplashTransition {
    if (state.stage == SplashStage.Completed) return SplashTransition(state)

    return when (action) {
        SplashAction.VisualPreparationCompleted -> {
            if (!state.visualReady) {
                SplashTransition(state.copy(visualReady = true, stage = SplashStage.Finishing))
            } else {
                SplashTransition(state)
            }
        }
        SplashAction.VisualFinished -> {
            if (state.stage != SplashStage.Finishing) {
                SplashTransition(state)
            } else {
                SplashTransition(
                    state = state.copy(stage = SplashStage.Completed),
                    events = listOf(SplashEvent.Navigate(normalDestination)),
                )
            }
        }
        is SplashAction.PermissionPauseChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.Permission, action.active))
        is SplashAction.ExternalLinkStateChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.ExternalLink, action.active))
    }
}

private fun SplashUiState.withPauseReason(
    reason: SplashPauseReason,
    active: Boolean,
): SplashUiState =
    copy(
        pauseReasons =
            if (active) {
                pauseReasons + reason
            } else {
                pauseReasons - reason
            },
    )
