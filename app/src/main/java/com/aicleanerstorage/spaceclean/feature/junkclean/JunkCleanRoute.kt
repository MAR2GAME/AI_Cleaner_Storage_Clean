package com.aicleanerstorage.spaceclean.feature.junkclean

import com.aicleanerstorage.spaceclean.feature.junkclean.*

import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanViewModel

import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanPhase

import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanEvent

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.AppPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.junkclean.ui.JunkCleanScreen

@Composable
fun JunkCleanRoute(
    viewModel: JunkCleanViewModel,
    permissionCoordinator: AppPermissionCoordinator,
    featureFlow: FeatureFlow,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateHomeAfterComplete: () -> Unit = onNavigateHome,
    onNavigateTool: (AppDestination) -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteAuthorizationLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            viewModel.handleAuthorizationResult(result.resultCode == Activity.RESULT_OK)
        }

    LaunchedEffect(viewModel, permissionCoordinator) {
        permissionCoordinator.ensure(
            action = ProtectedAction.JunkStartScan,
            onGranted = {
                viewModel.startScanIfNeeded()
            },
            onDenied = {
                featureFlow.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.PermissionRejected) {
                    viewModel.clearResult()
                    onNavigateHome()
                }
            },
        )
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (event.isJunkCleanCompletionSuccess()) {
                featureFlow.handleOperation(event) {
                    viewModel.showResult()
                }
            } else {
                featureFlow.handleOperation(event)
            }
        }
    }

    fun exitBack() {
        featureFlow.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.Return) {
            onNavigateBack()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is JunkCleanEvent.RequestDeleteAuthorization -> {
                    deleteAuthorizationLauncher.launch(
                        IntentSenderRequest.Builder(event.deleteRequest.intentSender).build(),
                    )
                }
            }
        }
    }

    fun exitToHome(afterComplete: Boolean = false) {
        if (afterComplete) {
            featureFlow.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.Return) {
                viewModel.clearResult()
                onNavigateHomeAfterComplete()
            }
        } else {
            viewModel.clearResult()
            onNavigateHome()
        }
    }

    fun handleBack() {
        when (uiState.phase) {
            JunkCleanPhase.Scanning -> {
                viewModel.requestStopDialog()
            }
            JunkCleanPhase.Cleaning -> {
                viewModel.requestStopDialog()
            }
            JunkCleanPhase.CompleteAnimation,
            JunkCleanPhase.Complete -> exitToHome(afterComplete = true)
            else -> exitBack()
        }
    }

    JunkCleanScreen(
        state = uiState,
        onNavigateTool = onNavigateTool,
        onAction = { action ->
            when (action) {
                JunkCleanUiAction.Back -> handleBack()
                JunkCleanUiAction.CleanSelected -> {
                    permissionCoordinator.ensure(ProtectedAction.JunkCleanSelected) {
                        viewModel.startCleaning(context)
                    }
                }
                is JunkCleanUiAction.ToggleCategories -> viewModel.toggleCategorySelection(action.categories)
                is JunkCleanUiAction.ToggleItem -> viewModel.toggleItemSelection(action.itemId)
                JunkCleanUiAction.ContinueFromResult -> exitToHome(afterComplete = true)
                JunkCleanUiAction.QuitStoppedOperation -> {
                    viewModel.dismissStopDialog(resume = false)
                    exitBack()
                }
                JunkCleanUiAction.ResumeStoppedOperation -> viewModel.dismissStopDialog(resume = true)
            }
        },
    )
}

private fun FeatureOperationEvent.isJunkCleanCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.JUNK_CLEAN &&
        action == OperationAction.CLEAN &&
        success
