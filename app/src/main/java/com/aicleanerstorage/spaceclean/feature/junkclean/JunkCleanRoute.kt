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
    val uiState by viewModel.state.collectAsStateWithLifecycle()
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
            viewModel.dispatch(JunkCleanIntent.DeleteAuthorizationResult(result.resultCode == Activity.RESULT_OK))
        }

    LaunchedEffect(viewModel) {
        viewModel.dispatch(JunkCleanIntent.Entered)
    }

    LaunchedEffect(viewModel, featureFlow) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is JunkCleanEffect.Operation -> {
                    if (!exitStarted) {
                        featureFlow.handleOperation(effect.event) {
                            if (
                                routeScope.isActive && !exitStarted &&
                                effect.event.isJunkCleanCompletionSuccess()
                            ) {
                                viewModel.dispatch(JunkCleanIntent.CompletionAdFinished)
                            }
                        }
                    }
                }
                is JunkCleanEffect.Exit -> {
                    exitOnce(effect.reason) {
                        viewModel.dispatch(JunkCleanIntent.ExitFinished)
                        when (effect.target) {
                            JunkExitTarget.Back -> onNavigateBack()
                            JunkExitTarget.HomeAfterComplete -> onNavigateHomeAfterComplete()
                        }
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

    JunkCleanScreen(
        state = uiState,
        interactionsEnabled = !exitStarted,
        onNavigateTool = { destination ->
            if (!exitStarted) onNavigateTool(destination)
        },
        onAction = { intent ->
            if (!exitStarted) viewModel.dispatch(intent)
        },
    )
}

private fun FeatureOperationEvent.isJunkCleanCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.JUNK_CLEAN &&
        action == OperationAction.CLEAN &&
        success
