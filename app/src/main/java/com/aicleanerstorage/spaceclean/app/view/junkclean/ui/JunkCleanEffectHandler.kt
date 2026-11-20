package com.aicleanerstorage.spaceclean.app.view.junkclean.ui

import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanEvent
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import kotlinx.coroutines.flow.Flow

suspend fun collectJunkCleanEvents(
    effects: Flow<JunkCleanEvent>,
    currentPhase: () -> JunkCleanPhase,
    isExiting: () -> Boolean,
    onExit: (JunkCleanEvent.Exit) -> Unit,
    onDeleteAuthorization: (JunkCleanEvent.RequestDeleteAuthorization) -> Unit,
) {
    effects.collect { effect ->
        when (effect) {
            is JunkCleanEvent.Exit -> {
                if (!isExiting()) onExit(effect)
            }
            is JunkCleanEvent.RequestDeleteAuthorization -> {
                if (!isExiting() && currentPhase() == JunkCleanPhase.AwaitingAuthorization) {
                    onDeleteAuthorization(effect)
                }
            }
        }
    }
}
