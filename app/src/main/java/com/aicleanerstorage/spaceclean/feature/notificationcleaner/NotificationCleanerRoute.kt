package com.aicleanerstorage.spaceclean.feature.notificationcleaner

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.ui.NotificationCleanerScreen

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

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionGateway = LocalPermissionGateway.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(NotificationCleanerAction.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { operationEvent ->
            val event = operationEvent.event
            if (exiting) return@collect
            when {
                event.isNotificationCleanerScanFinished() -> {
                    featureFlow.recordOperation(event)
                    operationEvent.scanGeneration?.let { scanGeneration ->
                        viewModel.onAction(NotificationCleanerAction.ShowStatus(scanGeneration))
                    }
                }
                event.isNotificationCleanerCompletionSuccess() -> {
                    if (!viewModel.uiState.value.completionAdInFlight && operationJob?.isActive != true) {
                        viewModel.onAction(NotificationCleanerAction.CompletionAdStarted)
                        operationJob = routeScope.launch {
                            featureFlow.handleOperation(event)
                            ensureActive()
                            if (!exiting) {
                                viewModel.onAction(NotificationCleanerAction.CompletionAdFinished)
                                viewModel.onAction(NotificationCleanerAction.ShowStatusAfterCompletionAd)
                            }
                        }
                    }
                }
                else -> routeScope.launch { featureFlow.handleOperation(event) }
            }
        }
    }

    fun exitBack() {
        run {
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
    }

    fun handleBack() {
        if (exiting) return
        if (state.completionAdInFlight) return
        when (state.page) {
            NotificationCleanerPage.Settings -> viewModel.onAction(NotificationCleanerAction.LeaveSettings)
            NotificationCleanerPage.Scanning,
            NotificationCleanerPage.Cleaning,
            -> viewModel.onAction(NotificationCleanerAction.RequestStop)
            else -> exitBack()
        }
    }

    NotificationCleanerScreen(
        state = state,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                NotificationCleanerAction.Back -> handleBack()
                NotificationCleanerAction.QuitStoppedOperation -> {
                    viewModel.onAction(action)
                    exitBack()
                }
                NotificationCleanerAction.EnableBlocking -> {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.NotificationCleanerEnable))) {
                                PermissionResult.Granted -> { viewModel.onAction(action) }
                                PermissionResult.Denied -> { Unit }
                                PermissionResult.Busy -> Unit
                            }
                        }
                    }
                }
                else -> viewModel.onAction(action)
            }
        },
        onNavigate = { if (!exiting) navigator.open(it) },
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
