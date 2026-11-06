package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageExitReason
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageEffect
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui.NetworkUsageRoute


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
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui.NetworkUsageScreen

@Composable
fun NetworkUsageRoute(
    navigator: AppNavigator,
    viewModel: NetworkUsageViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NetworkUsageEffect.Exit -> {
                    val reason = when (effect.reason) {
                        NetworkUsageExitReason.Return -> FeatureExitReason.Return
                        NetworkUsageExitReason.PermissionRejected -> FeatureExitReason.PermissionRejected
                    }
                    if (!exiting) {
                        exiting = true
                        routeScope.launch {
                            featureFlow.exit(FeatureKey.NETWORK_USAGE, reason)
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
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkUsageIntent.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(NetworkUsageIntent.Back) }

    NetworkUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

