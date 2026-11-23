package com.aicleanerstorage.spaceclean.app.view.junkclean.ui.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanEvent
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects

/**
 * Maps [JunkCleanViewModel.event] to page effects. Collection and lifecycle are
 * owned by [CollectEffects].
 */
@Composable
fun JunkCleanEffects(
    viewModel: JunkCleanViewModel,
    onExit: () -> Unit,
) {
    val latestOnExit by rememberUpdatedState(onExit)

    CollectEffects(viewModel) { effect ->
        when (effect) {
            JunkCleanEvent.Exit -> latestOnExit()
        }
    }
}
