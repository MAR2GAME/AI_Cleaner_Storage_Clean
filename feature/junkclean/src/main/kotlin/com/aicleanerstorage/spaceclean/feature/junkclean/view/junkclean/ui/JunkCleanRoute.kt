package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.ui

import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanIntent
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkExitTarget

import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanViewModel

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.ensureActive
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker

@Composable
fun JunkCleanRoute(
    viewModel: JunkCleanViewModel,
    featureFlow: FeatureFlow,
    uiRequestBroker: UiRequestBroker,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateHomeAfterComplete: () -> Unit = onNavigateHome,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var exitStarted by remember(viewModel) { mutableStateOf(false) }
    val routeScope = rememberCoroutineScope()
    var authorizationJob by remember(viewModel) { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    fun exitOnce(reason: FeatureExitReason, onContinue: () -> Unit) {
        if (exitStarted) return
        exitStarted = true
        authorizationJob?.cancel()
        routeScope.launch {
            featureFlow.exit(FeatureKey.JUNK_CLEAN, reason)
            ensureActive()
            onContinue()
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.dispatch(JunkCleanIntent.Entered)
    }

    LaunchedEffect(viewModel, featureFlow, uiRequestBroker) {
        collectJunkCleanEffects(
            effects = viewModel.effects,
            featureFlow = featureFlow,
            uiRequestBroker = uiRequestBroker,
            currentPhase = { viewModel.state.value.phase },
            isExiting = { exitStarted },
            onCompletionAdFinished = { viewModel.dispatch(JunkCleanIntent.CompletionAdFinished) },
            onExit = { effect ->
                exitOnce(effect.reason) {
                    viewModel.dispatch(JunkCleanIntent.ExitFinished)
                    when (effect.target) {
                        JunkExitTarget.Back -> onNavigateBack()
                        JunkExitTarget.HomeAfterComplete -> onNavigateHomeAfterComplete()
                    }
                }
            },
            onDeleteAuthorization = { effect ->
                if (authorizationJob?.isActive == true) return@collectJunkCleanEffects
                authorizationJob = routeScope.launch {
                    val result = uiRequestBroker.request(
                        com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest.MediaStoreDelete(
                            requestId = UiRequestBroker.newRequestId(),
                            uris = effect.uris,
                        ),
                    )
                    ensureActive()
                    if (exitStarted) return@launch
                    viewModel.dispatch(
                        JunkCleanIntent.DeleteAuthorizationResult(
                            result == com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult.Confirmed,
                        ),
                    )
                }
            },
        )
    }

    JunkCleanScreen(
        state = uiState,
        interactionsEnabled = !exitStarted,
        resultTools = resultTools,
        onAction = { intent ->
            if (!exitStarted) viewModel.dispatch(intent)
        },
    )
}
