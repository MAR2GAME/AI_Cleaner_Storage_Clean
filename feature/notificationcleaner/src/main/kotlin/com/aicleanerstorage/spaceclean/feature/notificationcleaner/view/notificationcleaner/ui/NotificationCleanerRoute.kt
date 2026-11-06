package com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.ui

import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult

import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerEffect
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerOperationEvent

import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerPage
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerIntent
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

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
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.core.ui.permission.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.ui.NotificationCleanerScreen

@Composable
fun NotificationCleanerRoute(
    navigator: AppNavigator,
    viewModel: NotificationCleanerViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var operationJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionGateway = LocalPermissionGateway.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(NotificationCleanerIntent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun exitBack() {
        if (!exiting) {
            exiting = true
            permissionJob?.cancel()
            operationJob?.cancel()
            routeScope.launch {
                featureFlow.exit(FeatureKey.NOTIFICATION_CLEANER, FeatureExitReason.Return)
                ensureActive()
                navigator.back()
            }
        }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NotificationCleanerEffect.Exit -> { exitBack(); return@collect }
                is NotificationCleanerEffect.Navigate -> { if (!exiting) navigator.navigate(effect.destination); return@collect }
                is NotificationCleanerOperationEvent -> Unit
            }
            val operationEvent = effect as NotificationCleanerOperationEvent
            val event = operationEvent.event
            if (exiting) return@collect
            when {
                event.isNotificationCleanerScanFinished() -> {
                    featureFlow.recordOperation(event)
                    operationEvent.scanGeneration?.let { scanGeneration ->
                        viewModel.dispatch(NotificationCleanerIntent.ShowStatus(scanGeneration))
                    }
                }
                event.isNotificationCleanerCompletionSuccess() -> {
                    if (!viewModel.state.value.completionAdInFlight && operationJob?.isActive != true) {
                        viewModel.dispatch(NotificationCleanerIntent.CompletionAdStarted)
                        operationJob = routeScope.launch {
                            featureFlow.handleOperation(event)
                            ensureActive()
                            if (!exiting) {
                                viewModel.dispatch(NotificationCleanerIntent.CompletionAdFinished)
                                viewModel.dispatch(NotificationCleanerIntent.ShowStatusAfterCompletionAd)
                            }
                        }
                    }
                }
                else -> routeScope.launch { featureFlow.handleOperation(event) }
            }
        }
    }





    NotificationCleanerScreen(
        state = state,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                NotificationCleanerIntent.Back -> viewModel.dispatch(action)
                NotificationCleanerIntent.QuitStoppedOperation -> {
                    viewModel.dispatch(action)
                }
                NotificationCleanerIntent.EnableBlocking -> {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.NotificationCleanerEnable)) {
                                GateResult.Allow -> { viewModel.dispatch(action) }
                                is GateResult.Deny,
                                GateResult.Busy -> Unit
                            }
                        }
                    }
                }
                else -> viewModel.dispatch(action)
            }
        },
        onNavigate = { if (!exiting) viewModel.dispatch(NotificationCleanerIntent.Navigate(it.toAppDestination())) },
    )
}

private fun FeatureOperationEvent.isNotificationCleanerScanFinished(): Boolean =
    this is FeatureOperationEvent.ScanFinished &&
        feature == FeatureKey.NOTIFICATION_CLEANER

private fun FeatureOperationEvent.isNotificationCleanerCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.NOTIFICATION_CLEANER &&
        action == OperationAction.CLEAN &&
        success
