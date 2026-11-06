package com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.ui

import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult

import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation.WhatsAppCleanerEffect

import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation.WhatsAppCleanerIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation.WhatsAppCleanerViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.ui.WhatsAppCleanerScreen

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

    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionGateway = LocalPermissionGateway.current

    fun exitOnce(reason: FeatureExitReason, navigate: () -> Unit) {
        if (exiting) return
        exiting = true
        permissionJob?.cancel()
        operationJob?.cancel()
        routeScope.launch {
            featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, reason)
            ensureActive()
            navigate()
        }
    }

    LaunchedEffect(viewModel, permissionGateway) {
        if (!exiting && permissionJob?.isActive != true) {
            permissionJob = routeScope.launch {
                when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.WhatsAppStartScan)) {
                    GateResult.Allow -> { viewModel.dispatch(WhatsAppCleanerIntent.StartScan) }
                    is GateResult.Deny -> exitOnce(FeatureExitReason.PermissionRejected) {
                        viewModel.dispatch(WhatsAppCleanerIntent.ClearResult)
                        navigator.backToMain()
                    }
                    GateResult.Busy -> Unit
                }
            }
        }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is WhatsAppCleanerEffect.Exit -> {
                    exitOnce(FeatureExitReason.Return) {
                        if (effect.home) { viewModel.dispatch(WhatsAppCleanerIntent.ClearResult); navigator.backToMain() }
                        else navigator.back()
                    }
                    return@collect
                }
                is WhatsAppCleanerEffect.Navigate -> { if (!exiting) navigator.navigate(effect.destination); return@collect }
                is WhatsAppCleanerEffect.Operation -> Unit
            }
            val event = (effect as WhatsAppCleanerEffect.Operation).event
            if (event.isWhatsAppCompletionSuccess()) {
                if (!exiting && operationJob?.isActive != true) {
                    operationJob = routeScope.launch {
                        featureFlow.handleOperation(event)
                        ensureActive()
                        if (!exiting) { viewModel.dispatch(WhatsAppCleanerIntent.ShowResult) }
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
                WhatsAppCleanerIntent.CleanSelected -> {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.WhatsAppCleanSelected)) {
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
        onNavigate = { if (!exiting) viewModel.dispatch(WhatsAppCleanerIntent.Navigate(it.toAppDestination())) },
    )
}

private fun FeatureOperationEvent.isWhatsAppCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.WHATSAPP_CLEANER &&
        action == OperationAction.CLEAN &&
        success

