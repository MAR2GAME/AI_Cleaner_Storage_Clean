package com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed

import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.*


import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.ui.NetworkSpeedScreen

@Composable
fun NetworkSpeedRoute(
    navigator: AppNavigator,
    viewModel: NetworkSpeedViewModel,
    featureFlow: FeatureFlow,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    fun handleBack() {
        if (uiState.phase == NetworkSpeedPhase.Completing) return
        if (uiState.phase == NetworkSpeedPhase.Testing) viewModel.stopSpeedTest()
        featureFlow.exit(FeatureKey.NETWORK_SPEED, FeatureExitReason.Return) { navigator.back() }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshNetworkStateUntilNetworkAvailable()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (event.isNetworkSpeedCompletionSuccess()) {
                featureFlow.handleOperation(event) {
                    viewModel.showResult()
                }
            } else {
                featureFlow.handleOperation(event)
            }
        }
    }

    BackHandler(onBack = ::handleBack)

    NetworkSpeedScreen(
        uiState = uiState,
        onBack = ::handleBack,
        onRunSpeedTest = viewModel::runSpeedTest,
        onStopSpeedTest = viewModel::stopSpeedTest,
        onNavigateTool = navigator::resetTo,
    )
}

private fun FeatureOperationEvent.isNetworkSpeedCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.NETWORK_SPEED &&
        action == OperationAction.TEST &&
        success
