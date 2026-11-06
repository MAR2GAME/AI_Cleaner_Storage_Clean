package com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui


import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockEffect
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockIntent
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*

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


import com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui.AppLockScreen

@Composable
fun AppLockRoute(
    navigator: AppNavigator,
    viewModel: AppLockViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
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
        if (uiState.overlayPermissionRequired) viewModel.dispatch(AppLockIntent.ConsumeOverlayPermissionRequest)
    }

    fun exitBack() {
        if (!exiting) {
            exiting = true
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
            is AppLockIntent.TogglePackage,
            AppLockIntent.ToggleAllApps,
            AppLockIntent.BeginCreatePin,
            is AppLockIntent.MonitoringChanged -> viewModel.dispatch(action)
            else -> viewModel.dispatch(action)
        }
    }

    AppLockScreen(
        state = uiState,
        onAction = ::handleAction,
        onNavigate = { if (!exiting) viewModel.dispatch(AppLockIntent.Navigate(it.toAppDestination())) },
    )
}
