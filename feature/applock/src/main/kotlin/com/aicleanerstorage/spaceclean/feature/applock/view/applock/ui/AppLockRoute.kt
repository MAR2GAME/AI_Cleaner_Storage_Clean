package com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui

import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult

import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockEffect
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockIntent
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.core.ui.permission.LocalPermissionGateway


import com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui.AppLockScreen

@Composable
fun AppLockRoute(
    navigator: AppNavigator,
    viewModel: AppLockViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permissionGateway = LocalPermissionGateway.current
    val toastRes = uiState.toastRes
    val toastMessage = toastRes?.let { stringResource(it) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(AppLockIntent.RefreshAfterResume)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.dispatch(AppLockIntent.ConsumeToast)
        }
    }

    LaunchedEffect(uiState.overlayPermissionRequired) {
        if (uiState.overlayPermissionRequired) {
            if (!exiting && permissionJob?.isActive != true) {
                permissionJob = routeScope.launch {
                    when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.AppLockRequestOverlay)) {
                        GateResult.Allow -> { viewModel.dispatch(AppLockIntent.ConsumeOverlayPermissionRequest) }
                        is GateResult.Deny -> { viewModel.dispatch(AppLockIntent.ConsumeOverlayPermissionRequest) }
                        GateResult.Busy -> Unit
                    }
                }
            }
        }
    }

    fun exitBack() {
        if (!exiting) {
            exiting = true
            permissionJob?.cancel()
            routeScope.launch {
                featureFlow.exit(FeatureKey.APP_LOCK, FeatureExitReason.Return)
                ensureActive()
                navigator.back()
            }
        }
    }

    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AppLockEffect.Exit -> exitBack()
                is AppLockEffect.Navigate -> if (!exiting) navigator.navigate(effect.destination)
            }
        }
    }

    fun handleAction(action: AppLockIntent) {
        if (exiting) return
        when (action) {
            AppLockIntent.Back -> viewModel.dispatch(action)
            is AppLockIntent.TogglePackage -> {
                if (uiState.page == AppLockPage.SelectApps) {
                    viewModel.dispatch(action)
                } else {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.AppLockOpenProtectedArea)) {
                                GateResult.Allow -> { viewModel.dispatch(action) }
                                is GateResult.Deny,
                                GateResult.Busy -> Unit
                            }
                        }
                    }
                }
            }
            AppLockIntent.ToggleAllApps,
            AppLockIntent.BeginCreatePin -> {
                if (!exiting && permissionJob?.isActive != true) {
                    permissionJob = routeScope.launch {
                        when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.AppLockOpenProtectedArea)) {
                            GateResult.Allow -> { viewModel.dispatch(action) }
                            is GateResult.Deny,
                            GateResult.Busy -> Unit
                        }
                    }
                }
            }
            is AppLockIntent.MonitoringChanged -> {
                if (!action.enabled) {
                    viewModel.dispatch(action)
                } else {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.AppLockEnableMonitoring)) {
                                GateResult.Allow -> { viewModel.dispatch(action) }
                                is GateResult.Deny,
                                GateResult.Busy -> Unit
                            }
                        }
                    }
                }
            }
            else -> viewModel.dispatch(action)
        }
    }

    AppLockScreen(
        state = uiState,
        onAction = ::handleAction,
        onNavigate = { if (!exiting) viewModel.dispatch(AppLockIntent.Navigate(it.toAppDestination())) },
    )
}
