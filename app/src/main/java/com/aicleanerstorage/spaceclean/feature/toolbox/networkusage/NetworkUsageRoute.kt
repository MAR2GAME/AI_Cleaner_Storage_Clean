package com.aicleanerstorage.spaceclean.feature.toolbox.networkusage

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
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.ui.NetworkUsageScreen

@Composable
fun NetworkUsageRoute(
    navigator: AppNavigator,
    viewModel: NetworkUsageViewModel,
    featureFlow: FeatureFlow,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionCoordinator = LocalPermissionCoordinator.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NetworkUsageEffect.Exit -> {
                    val reason = when (effect.reason) {
                        NetworkUsageExitReason.Return -> FeatureExitReason.Return
                        NetworkUsageExitReason.PermissionRejected -> FeatureExitReason.PermissionRejected
                    }
                    featureFlow.exit(FeatureKey.NETWORK_USAGE, reason) { navigator.back() }
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
            permissionCoordinator.ensure(
                permission = PermissionType.UsageAccess,
                onGranted = { viewModel.onAction(NetworkUsageAction.PermissionGranted) },
                onDenied = { viewModel.onAction(NetworkUsageAction.PermissionRejected) },
            )
        }
    }

    BackHandler { viewModel.onAction(NetworkUsageAction.Back) }

    NetworkUsageScreen(
        state = uiState,
        onAction = viewModel::onAction,
    )
}
