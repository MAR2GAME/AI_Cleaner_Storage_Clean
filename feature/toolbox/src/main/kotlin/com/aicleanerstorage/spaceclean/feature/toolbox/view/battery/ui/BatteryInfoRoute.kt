package com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoEffect

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
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.ui.BatteryInfoScreen

@Composable
fun BatteryInfoRoute(
    navigator: AppNavigator,
    viewModel: BatteryInfoViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BatteryInfoEffect.Exit ->
                    {
                        if (!exiting) {
                            exiting = true
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.BATTERY_INFO, FeatureExitReason.Return)
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
                    Lifecycle.Event.ON_RESUME -> viewModel.dispatch(BatteryInfoIntent.Resumed)
                    Lifecycle.Event.ON_PAUSE,
                    Lifecycle.Event.ON_STOP,
                    -> viewModel.dispatch(BatteryInfoIntent.Paused)
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.dispatch(BatteryInfoIntent.Paused)
        }
    }

    BackHandler { viewModel.dispatch(BatteryInfoIntent.Back) }

    BatteryInfoScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

