package com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoEffect

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.ui.DeviceInfoRoute

import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.ui.DeviceInfoScreen

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

@Composable
fun DeviceInfoRoute(
    navigator: AppNavigator,
    viewModel: DeviceInfoViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DeviceInfoEffect.Exit ->
                    {
                        if (!exiting) {
                            exiting = true
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.DEVICE_INFO, FeatureExitReason.Return)
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
                if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(DeviceInfoIntent.Resumed)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(DeviceInfoIntent.Back) }

    DeviceInfoScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

