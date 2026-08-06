package com.aicleanerstorage.spaceclean.feature.notificationcleaner

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
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.ui.NotificationCleanerScreen

@Composable
fun NotificationCleanerRoute(
    navigator: AppNavigator,
    viewModel: NotificationCleanerViewModel,
    featureFlow: FeatureFlow,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionCoordinator = LocalPermissionCoordinator.current

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
            when {
                event.isNotificationCleanerScanFinished() -> {
                    featureFlow.handleOperation(event) {
                        operationEvent.scanGeneration?.let { scanGeneration ->
                            viewModel.onAction(NotificationCleanerAction.ShowStatus(scanGeneration))
                        }
                    }
                }
                event.isNotificationCleanerCompletionSuccess() -> {
                    if (!viewModel.uiState.value.completionAdInFlight) {
                        viewModel.onAction(NotificationCleanerAction.CompletionAdStarted)
                        featureFlow.handleOperation(event) {
                            viewModel.onAction(NotificationCleanerAction.CompletionAdFinished)
                            viewModel.onAction(NotificationCleanerAction.ShowStatusAfterCompletionAd)
                        }
                    }
                }
                else -> {
                    featureFlow.handleOperation(event)
                }
            }
        }
    }

    fun exitBack() {
        featureFlow.exit(FeatureKey.NOTIFICATION_CLEANER, FeatureExitReason.Return) { navigator.back() }
    }

    fun handleBack() {
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
        onAction = { action ->
            when (action) {
                NotificationCleanerAction.Back -> handleBack()
                NotificationCleanerAction.QuitStoppedOperation -> {
                    viewModel.onAction(action)
                    exitBack()
                }
                NotificationCleanerAction.EnableBlocking -> {
                    permissionCoordinator.ensure(ProtectedAction.NotificationCleanerEnable) {
                        viewModel.onAction(action)
                    }
                }
                else -> viewModel.onAction(action)
            }
        },
        onNavigate = navigator::open,
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
