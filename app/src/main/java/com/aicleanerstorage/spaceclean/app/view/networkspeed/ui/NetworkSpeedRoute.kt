package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui

import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedEvent

import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedAction


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedRoute


import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedScreen

@Composable
fun NetworkSpeedRoute(
    navigator: AppNavigator,
    viewModel: NetworkSpeedViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
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

    LaunchedEffect(viewModel) {
        viewModel.event.collect { effect ->
            when (effect) {
                is NetworkSpeedEvent.Exit -> handleBack()
                is NetworkSpeedEvent.Navigate -> if (!exiting) navigator.resetToRoot(effect.destination)
            }
        }
    }

    BackHandler { viewModel.dispatch(NetworkSpeedAction.BackClicked) }

    NetworkSpeedScreen(
        uiState = uiState,
        onBack = { viewModel.dispatch(NetworkSpeedAction.BackClicked) },
        onRunSpeedTest = { viewModel.dispatch(NetworkSpeedAction.SpeedTestRequested) },
        onStopSpeedTest = { viewModel.dispatch(NetworkSpeedAction.StopClicked) },
        onNavigateTool = { if (!exiting) viewModel.dispatch(NetworkSpeedAction.NavigationRequested(it)) },
    )
}
