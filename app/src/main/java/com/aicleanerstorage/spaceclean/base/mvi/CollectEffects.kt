package com.aicleanerstorage.spaceclean.base.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

/**
 * Single consumer for one-shot effects.
 *
 * Collects only while the screen is RESUMED; effects emitted in the background stay
 * in the channel and are delivered after the screen comes back.
 */
@Composable
fun <Event : Any> CollectEffects(
    viewModel: MviViewModel<*, *, Event>,
    onEvent: (Event) -> Unit,
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.event.collect { currentOnEvent(it) }
        }
    }
}
