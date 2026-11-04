package com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerAction
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.ui.WhatsAppCleanerScreen

@Composable
fun WhatsAppCleanerRoute(
    navigator: AppNavigator,
    viewModel: WhatsAppCleanerViewModel,
    featureFlow: FeatureFlow,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var operationJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionGateway = LocalPermissionGateway.current

    fun exitToHomeAfterPermissionRejected() {
        run {
            if (!exiting) {
                exiting = true
                permissionJob?.cancel()
                operationJob?.cancel()
                routeScope.launch {
                    featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.PermissionRejected)
                    ensureActive()
                    viewModel.onAction(WhatsAppCleanerAction.ClearResult)
                    navigator.home()
                }
            }
        }
    }

    LaunchedEffect(viewModel, permissionGateway) {
        if (!exiting && permissionJob?.isActive != true) {
            permissionJob = routeScope.launch {
                when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.WhatsAppStartScan))) {
                    PermissionResult.Granted -> { viewModel.onAction(WhatsAppCleanerAction.StartScan) }
                    PermissionResult.Denied -> { exitToHomeAfterPermissionRejected() }
                    PermissionResult.Busy -> Unit
                }
            }
        }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (event.isWhatsAppCompletionSuccess()) {
                if (!exiting && operationJob?.isActive != true) {
                    operationJob = routeScope.launch {
                        featureFlow.handleOperation(event)
                        ensureActive()
                        if (!exiting) { viewModel.onAction(WhatsAppCleanerAction.ShowResult) }
                    }
                }
            } else {
                if (!exiting) routeScope.launch { if (!exiting) featureFlow.handleOperation(event) }
            }
        }
    }

    WhatsAppCleanerScreen(
        state = state,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                WhatsAppCleanerAction.Back -> {
                    run {
                        if (!exiting) {
                            exiting = true
                            permissionJob?.cancel()
                            operationJob?.cancel()
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.Return)
                                ensureActive()
                                navigator.back()
                            }
                        }
                    }
                }
                WhatsAppCleanerAction.ExitAfterComplete -> {
                    run {
                        if (!exiting) {
                            exiting = true
                            permissionJob?.cancel()
                            operationJob?.cancel()
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.Return)
                                ensureActive()
                                viewModel.onAction(WhatsAppCleanerAction.ClearResult)
                                navigator.home()
                            }
                        }
                    }
                }
                WhatsAppCleanerAction.CleanSelected -> {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.WhatsAppCleanSelected))) {
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

private fun FeatureOperationEvent.isWhatsAppCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.WHATSAPP_CLEANER &&
        action == OperationAction.CLEAN &&
        success
