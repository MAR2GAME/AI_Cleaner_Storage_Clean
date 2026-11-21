package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

internal data class SplashTransition(
    val state: SplashUiState,
    val events: List<SplashEvent> = emptyList(),
)

/** Pure transition table of the splash flow: state + action in, state + events out. */
internal object SplashReducer {
    fun reduce(
        state: SplashUiState,
        action: SplashAction,
        normalDestination: AppDestination,
    ): SplashTransition {
        if (state.stage == SplashStage.Completed) return SplashTransition(state)

        return when (action) {
            SplashAction.VisualPreparationCompleted ->
                if (state.stage == SplashStage.Preparing) {
                    SplashTransition(state.copy(stage = SplashStage.Finishing))
                } else {
                    SplashTransition(state)
                }

            SplashAction.VisualFinished ->
                if (state.stage != SplashStage.Finishing) {
                    SplashTransition(state)
                } else {
                    SplashTransition(
                        state = state.copy(stage = SplashStage.Completed),
                        events = listOf(SplashEvent.Navigate(normalDestination)),
                    )
                }

            SplashAction.TermsClicked ->
                SplashTransition(
                    state = state.withPauseReason(SplashPauseReason.ExternalLink, true),
                    events = listOf(SplashEvent.OpenTerms),
                )

            SplashAction.PrivacyClicked ->
                SplashTransition(
                    state = state.withPauseReason(SplashPauseReason.ExternalLink, true),
                    events = listOf(SplashEvent.OpenPrivacy),
                )

            is SplashAction.PermissionPauseChanged ->
                SplashTransition(state.withPauseReason(SplashPauseReason.Permission, action.active))

            is SplashAction.ExternalLinkStateChanged ->
                SplashTransition(state.withPauseReason(SplashPauseReason.ExternalLink, action.active))
        }
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
