package com.aicleanerstorage.spaceclean.feature.junkclean

import com.aicleanerstorage.spaceclean.feature.junkclean.*

import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanViewModel

import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanPhase


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.isActive
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.feature.junkclean.ui.JunkCleanScreen

@Composable
fun JunkCleanRoute(
    viewModel: JunkCleanViewModel,
    featureFlow: FeatureFlow,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateHomeAfterComplete: () -> Unit = onNavigateHome,
    onNavigateTool: (AppDestination) -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var exitStarted by remember(viewModel) { mutableStateOf(false) }
    val routeScope = rememberCoroutineScope()

    fun exitOnce(reason: FeatureExitReason, onContinue: () -> Unit) {
        if (exitStarted) return
        exitStarted = true
        featureFlow.exit(FeatureKey.JUNK_CLEAN, reason) {
            if (routeScope.isActive) onContinue()
        }
    }
    val deleteAuthorizationLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            viewModel.handleAuthorizationResult(result.resultCode == Activity.RESULT_OK)
        }

    LaunchedEffect(viewModel) {
        viewModel.startScanIfNeeded()
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
        exitOnce(FeatureExitReason.Return) {
            onNavigateBack()
        }
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is JunkCleanEffect.Exit -> {
                    exitOnce(effect.reason) {
                        viewModel.clearSharedResults()
                        onNavigateBack()
                    }
                }
                is JunkCleanEffect.RequestDeleteAuthorization -> {
                    if (!exitStarted) deleteAuthorizationLauncher.launch(
                        IntentSenderRequest.Builder(effect.deleteRequest.intentSender).build(),
                    )
                }
            }
        }
    }

    fun exitToHome(afterComplete: Boolean = false) {
        if (exitStarted) return
        if (afterComplete) {
            exitOnce(FeatureExitReason.Return) {
                viewModel.clearSharedResults()
                onNavigateHomeAfterComplete()
            }
        } else {
            exitStarted = true
            viewModel.clearSharedResults()
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
        interactionsEnabled = !exitStarted,
        onNavigateTool = { destination ->
            if (!exitStarted) onNavigateTool(destination)
        },
        onAction = { action ->
            if (!exitStarted) when (action) {
                JunkCleanUiAction.Back -> handleBack()
                JunkCleanUiAction.CleanSelected -> {
                    viewModel.startCleaning(context)
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
