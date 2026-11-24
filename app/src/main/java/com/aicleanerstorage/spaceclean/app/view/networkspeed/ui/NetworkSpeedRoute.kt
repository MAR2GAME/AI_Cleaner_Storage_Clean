package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2CompletionAdEffect
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedAction
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedEvent
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedRoute
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedScreen
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun NetworkSpeedRoute(
    navigator: AppNavigator,
    viewModel: NetworkSpeedViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    Mar2CompletionAdEffect(
        show = uiState.phase == NetworkSpeedPhase.Result,
        areaKey = Mar2AdPlacementRegistry.MAIN_NETWORK_SPEED_FINISH,
        destination = AppDestination.NetworkSpeed,
    )

    fun handleBack() {
        if (exiting) return
        if (!exiting) {
            exiting = true
            routeScope.launch {
                ensureActive()
                navigator.back()
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkSpeedAction.NetworkRefreshRequested)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            is NetworkSpeedEvent.Exit -> handleBack()
            is NetworkSpeedEvent.Navigate -> if (!exiting) navigator.resetToRoot(effect.destination)
        }
    }

    NetworkSpeedScreen(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                is NetworkSpeedAction.NavigationRequested -> if (!exiting) viewModel.dispatch(action)
                else -> viewModel.dispatch(action)
            }
        },
    )
}
