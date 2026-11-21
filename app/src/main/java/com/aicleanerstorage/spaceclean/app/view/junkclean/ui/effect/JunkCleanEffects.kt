package com.aicleanerstorage.spaceclean.app.view.junkclean.ui.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanEvent
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects

/**
 * Maps [JunkCleanViewModel.event] to page effects. Collection and lifecycle are
 * owned by [CollectEffects]; stale authorization prompts are filtered here.
 */
@Composable
fun JunkCleanEffects(
    viewModel: JunkCleanViewModel,
    onExit: (JunkCleanEvent.Exit) -> Unit,
    onDeleteAuthorization: (JunkCleanEvent.RequestDeleteAuthorization) -> Unit,
) {
    val latestOnExit by rememberUpdatedState(onExit)
    val latestOnDeleteAuthorization by rememberUpdatedState(onDeleteAuthorization)

    CollectEffects(viewModel) { effect ->
        when (effect) {
            is JunkCleanEvent.Exit -> latestOnExit(effect)
            is JunkCleanEvent.RequestDeleteAuthorization ->
                if (viewModel.state.value.phase == JunkCleanPhase.AwaitingAuthorization) {
                    latestOnDeleteAuthorization(effect)
                }
        }
    }
}
