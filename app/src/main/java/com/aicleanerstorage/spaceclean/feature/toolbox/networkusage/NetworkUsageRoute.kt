package com.aicleanerstorage.spaceclean.feature.toolbox.networkusage

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.*


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
import com.aicleanerstorage.spaceclean.common.permission.PermissionType
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.ui.NetworkUsageScreen

@Composable
fun NetworkUsageRoute(
    navigator: AppNavigator,
    viewModel: NetworkUsageViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionGateway = LocalPermissionGateway.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NetworkUsageEffect.Exit -> {
                    val reason = when (effect.reason) {
                        NetworkUsageExitReason.Return -> FeatureExitReason.Return
                        NetworkUsageExitReason.PermissionRejected -> FeatureExitReason.PermissionRejected
                    }
                    run {
                        if (!exiting) {
                            exiting = true
                            permissionJob?.cancel()
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
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.onAction(NetworkUsageAction.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.permissionRequestPending) {
        if (uiState.permissionRequestPending) {
            viewModel.onAction(NetworkUsageAction.PermissionRequestConsumed)
            if (!exiting && permissionJob?.isActive != true) {
                permissionJob = routeScope.launch {
                    when (permissionGateway.ensure(PermissionTarget.Permission(PermissionType.UsageAccess))) {
                        PermissionResult.Granted -> { viewModel.onAction(NetworkUsageAction.PermissionGranted) }
                        PermissionResult.Denied -> { viewModel.onAction(NetworkUsageAction.PermissionRejected) }
                        PermissionResult.Busy -> Unit
                    }
                }
            }
        }
    }

    BackHandler { viewModel.onAction(NetworkUsageAction.Back) }

    NetworkUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.onAction(it) },
    )
}
