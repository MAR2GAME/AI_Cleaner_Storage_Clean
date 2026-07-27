package com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner

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
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerAction
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.ui.WhatsAppCleanerScreen

@Composable
fun WhatsAppCleanerRoute(
    navigator: AppNavigator,
    viewModel: WhatsAppCleanerViewModel,
    featureFlow: FeatureFlow,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionCoordinator = LocalPermissionCoordinator.current

    fun exitToHomeAfterPermissionRejected() {
        featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.PermissionRejected) {
            viewModel.onAction(WhatsAppCleanerAction.ClearResult)
            navigator.home()
        }
    }

    LaunchedEffect(viewModel, permissionCoordinator) {
        permissionCoordinator.ensure(
            action = ProtectedAction.WhatsAppStartScan,
            onGranted = { viewModel.onAction(WhatsAppCleanerAction.StartScan) },
            onDenied = ::exitToHomeAfterPermissionRejected,
        )
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (event.isWhatsAppCompletionSuccess()) {
                featureFlow.handleOperation(event) {
                    viewModel.onAction(WhatsAppCleanerAction.ShowResult)
                }
            } else {
                featureFlow.handleOperation(event)
            }
        }
    }

    WhatsAppCleanerScreen(
        state = state,
        onAction = { action ->
            when (action) {
                WhatsAppCleanerAction.Back -> {
                    featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.Return) { navigator.back() }
                }
                WhatsAppCleanerAction.ExitAfterComplete -> {
                    featureFlow.exit(FeatureKey.WHATSAPP_CLEANER, FeatureExitReason.Return) {
                        viewModel.onAction(WhatsAppCleanerAction.ClearResult)
                        navigator.home()
                    }
                }
                WhatsAppCleanerAction.CleanSelected -> {
                    permissionCoordinator.ensure(ProtectedAction.WhatsAppCleanSelected) {
                        viewModel.onAction(action)
                    }
                }
                else -> viewModel.onAction(action)
            }
        },
        onNavigate = navigator::open,
    )
}

private fun FeatureOperationEvent.isWhatsAppCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.WHATSAPP_CLEANER &&
        action == OperationAction.CLEAN &&
        success
