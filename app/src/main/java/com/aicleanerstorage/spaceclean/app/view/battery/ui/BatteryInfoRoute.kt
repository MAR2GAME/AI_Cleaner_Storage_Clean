package com.aicleanerstorage.spaceclean.app.view.battery.ui

import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoAction
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoEvent

import androidx.compose.runtime.*
import kotlinx.coroutines.*

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
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryInfoScreen

@Composable
fun BatteryInfoRoute(
    navigator: AppNavigator,
    viewModel: BatteryInfoViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                BatteryInfoEvent.Exit ->
                    {
                        if (!exiting) {
                            exiting = true
                            routeScope.launch {
                                ensureActive()
                                navigator.back()
                            }
                        }
                    }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> viewModel.dispatch(BatteryInfoAction.Resumed)
                    Lifecycle.Event.ON_PAUSE,
                    Lifecycle.Event.ON_STOP,
                    -> viewModel.dispatch(BatteryInfoAction.Paused)
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.dispatch(BatteryInfoAction.Paused)
        }
    }

    BackHandler { viewModel.dispatch(BatteryInfoAction.BackClicked) }

    BatteryInfoScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

