package com.aicleanerstorage.spaceclean.app.view.applock.ui


import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockEvent
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockPage
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockAction
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

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


import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockScreen

@Composable
fun AppLockRoute(
    navigator: AppNavigator,
    viewModel: AppLockViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permissionGate = rememberPermissionGate()
    val toastRes = uiState.toastRes
    val toastMessage = toastRes?.let { stringResource(it) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(AppLockAction.Resumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.dispatch(AppLockAction.ToastShown)
        }
    }

    LaunchedEffect(uiState.overlayPermissionRequired) {
        if (uiState.overlayPermissionRequired) {
            viewModel.dispatch(AppLockAction.OverlayPermissionRequestHandled)
            // 授权结果�?ON_RESUME �?RefreshAfterResume 统一复检
            permissionGate.allow(AppPermission.Overlay)
        }
    }

    fun exitBack() {
        if (!exiting) {
            exiting = true
            routeScope.launch {
                ensureActive()
                navigator.back()
            }
        }
    }

    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                AppLockEvent.Exit -> exitBack()
                is AppLockEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
            }
        }
    }

    fun handleAction(action: AppLockAction) {
        if (exiting) return
        when (action) {
            AppLockAction.BackClicked -> viewModel.dispatch(action)
            is AppLockAction.PackageToggled,
            AppLockAction.AllAppsSelectionToggled,
            AppLockAction.PinCreationRequested,
            is AppLockAction.MonitoringChanged -> viewModel.dispatch(action)
            else -> viewModel.dispatch(action)
        }
    }

    AppLockScreen(
        state = uiState,
        onAction = ::handleAction,
        onNavigate = { if (!exiting) viewModel.dispatch(AppLockAction.NavigationRequested(it)) },
    )
}
