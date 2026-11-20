package com.aicleanerstorage.spaceclean.app.view.networkusage.ui

import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageExitReason
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageEvent
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageAction
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.NetworkUsageRoute


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
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.NetworkUsageScreen

@Composable
fun NetworkUsageRoute(
    navigator: AppNavigator,
    viewModel: NetworkUsageViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionGate = rememberPermissionGate()
    LaunchedEffect(uiState.permissionRequestPending) {
        if (uiState.permissionRequestPending) {
            viewModel.dispatch(NetworkUsageAction.PermissionRequestConsumed)
            permissionGate.request(AppPermission.UsageAccess) { granted ->
                viewModel.dispatch(if (granted) NetworkUsageAction.PermissionGranted else NetworkUsageAction.PermissionRejected)
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                is NetworkUsageEvent.Exit -> {
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
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkUsageAction.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(NetworkUsageAction.BackClicked) }

    NetworkUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

