package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedEffect

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedIntent


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui.NetworkSpeedRoute


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
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui.NetworkSpeedScreen

@Composable
fun NetworkSpeedRoute(
    navigator: AppNavigator,
    viewModel: NetworkSpeedViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var operationJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    fun handleBack() {
        if (exiting) return
        if (!exiting) {
            exiting = true
            operationJob?.cancel()
            routeScope.launch {
                featureFlow.exit(FeatureKey.NETWORK_SPEED, FeatureExitReason.Return)
                ensureActive()
                navigator.back()
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkSpeedIntent.RefreshNetworkStateUntilNetworkAvailable)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NetworkSpeedEffect.Exit -> { handleBack(); return@collect }
                is NetworkSpeedEffect.Navigate -> { if (!exiting) navigator.resetTo(effect.destination); return@collect }
                is NetworkSpeedEffect.Operation -> Unit
            }
            val event = (effect as NetworkSpeedEffect.Operation).event
            if (event.isNetworkSpeedCompletionSuccess()) {
                if (!exiting && operationJob?.isActive != true) {
                    operationJob = routeScope.launch {
                        featureFlow.handleOperation(event)
                        ensureActive()
                        if (!exiting) { viewModel.dispatch(NetworkSpeedIntent.ShowResult) }
                    }
                }
            } else {
                if (!exiting) routeScope.launch { if (!exiting) featureFlow.handleOperation(event) }
            }
        }
    }

    BackHandler { viewModel.dispatch(NetworkSpeedIntent.Back) }

    NetworkSpeedScreen(
        uiState = uiState,
        onBack = { viewModel.dispatch(NetworkSpeedIntent.Back) },
        onRunSpeedTest = { viewModel.dispatch(NetworkSpeedIntent.RunSpeedTest) },
        onStopSpeedTest = { viewModel.dispatch(NetworkSpeedIntent.StopSpeedTest) },
        onNavigateTool = { if (!exiting) viewModel.dispatch(NetworkSpeedIntent.Navigate(it.toAppDestination())) },
    )
}

private fun FeatureOperationEvent.isNetworkSpeedCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.NETWORK_SPEED &&
        action == OperationAction.TEST &&
        success

