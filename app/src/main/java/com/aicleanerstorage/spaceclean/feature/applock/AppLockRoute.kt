package com.aicleanerstorage.spaceclean.feature.applock

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import com.aicleanerstorage.spaceclean.feature.applock.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.AppLockPinStep
import com.aicleanerstorage.spaceclean.feature.applock.AppLockAction
import com.aicleanerstorage.spaceclean.feature.applock.AppLockUiState
import com.aicleanerstorage.spaceclean.feature.applock.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway


import com.aicleanerstorage.spaceclean.feature.applock.ui.AppLockScreen

@Composable
internal fun AppLockRoute(
    navigator: AppNavigator,
    viewModel: AppLockViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permissionGateway = LocalPermissionGateway.current
    val toastRes = uiState.toastRes
    val toastMessage = toastRes?.let { stringResource(it) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(AppLockAction.RefreshAfterResume)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.onAction(AppLockAction.ConsumeToast)
        }
    }

    LaunchedEffect(uiState.overlayPermissionRequired) {
        if (uiState.overlayPermissionRequired) {
            if (!exiting && permissionJob?.isActive != true) {
                permissionJob = routeScope.launch {
                    when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.AppLockRequestOverlay))) {
                        PermissionResult.Granted -> { viewModel.onAction(AppLockAction.ConsumeOverlayPermissionRequest) }
                        PermissionResult.Denied -> { viewModel.onAction(AppLockAction.ConsumeOverlayPermissionRequest) }
                        PermissionResult.Busy -> Unit
                    }
                }
            }
        }
    }

    fun exitBack() {
        run {
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
    }

    fun handleAction(action: AppLockAction) {
        if (exiting) return
        when (action) {
            AppLockAction.Back -> {
                if (!viewModel.handleBack()) exitBack()
            }
            is AppLockAction.TogglePackage -> {
                if (uiState.page == AppLockPage.SelectApps) {
                    viewModel.onAction(action)
                } else {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.AppLockOpenProtectedArea))) {
                                PermissionResult.Granted -> { viewModel.onAction(action) }
                                PermissionResult.Denied -> { Unit }
                                PermissionResult.Busy -> Unit
                            }
                        }
                    }
                }
            }
            AppLockAction.ToggleAllApps,
            AppLockAction.BeginCreatePin -> {
                if (!exiting && permissionJob?.isActive != true) {
                    permissionJob = routeScope.launch {
                        when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.AppLockOpenProtectedArea))) {
                            PermissionResult.Granted -> { viewModel.onAction(action) }
                            PermissionResult.Denied -> { Unit }
                            PermissionResult.Busy -> Unit
                        }
                    }
                }
            }
            is AppLockAction.MonitoringChanged -> {
                if (!action.enabled) {
                    viewModel.onAction(action)
                } else {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.AppLockEnableMonitoring))) {
                                PermissionResult.Granted -> { viewModel.onAction(action) }
                                PermissionResult.Denied -> { Unit }
                                PermissionResult.Busy -> Unit
                            }
                        }
                    }
                }
            }
            else -> viewModel.onAction(action)
        }
    }

    AppLockScreen(
        state = uiState,
        onAction = ::handleAction,
        onNavigate = { if (!exiting) navigator.open(it) },
    )
}
