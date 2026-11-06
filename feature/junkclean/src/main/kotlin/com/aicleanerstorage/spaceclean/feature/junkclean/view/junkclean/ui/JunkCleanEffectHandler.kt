package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.ui

import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanEffect
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanPhase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

suspend fun collectJunkCleanEffects(
    effects: Flow<JunkCleanEffect>,
    featureFlow: FeatureFlow,
    uiRequestBroker: UiRequestBroker,
    currentPhase: () -> JunkCleanPhase,
    isExiting: () -> Boolean,
    onCompletionAdFinished: () -> Unit,
    onExit: (JunkCleanEffect.Exit) -> Unit,
    onDeleteAuthorization: (JunkCleanEffect.RequestDeleteAuthorization) -> Unit,
): Unit = coroutineScope {
    var completionAdJob: Job? = null
    effects.collect { effect ->
        when (effect) {
            is JunkCleanEffect.Operation -> {
                if (isExiting()) return@collect
                if (effect.event.isJunkCleanCompletionSuccess()) {
                    if (currentPhase() != JunkCleanPhase.CompleteAnimation || completionAdJob != null) return@collect
                    completionAdJob = launch(start = CoroutineStart.LAZY) {
                        val result = uiRequestBroker.request(
                            UiRequest.ShowInterstitial(
                                requestId = UiRequestBroker.newRequestId(),
                                placement = "junk_clean_completion",
                            ),
                        )
                        currentCoroutineContext().ensureActive()
                        if (
                            result != com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult.Cancelled &&
                            !isExiting() &&
                            currentPhase() == JunkCleanPhase.CompleteAnimation
                        ) {
                            onCompletionAdFinished()
                        }
                    }
                    featureFlow.recordOperation(effect.event)
                    completionAdJob?.start()
                } else {
                    launch { if (!isExiting()) featureFlow.handleOperation(effect.event) }
                }
            }
            is JunkCleanEffect.Exit -> {
                completionAdJob?.cancel()
                if (!isExiting()) onExit(effect)
            }
            is JunkCleanEffect.RequestDeleteAuthorization -> {
                if (!isExiting() && currentPhase() == JunkCleanPhase.AwaitingAuthorization) {
                    onDeleteAuthorization(effect)
                }
            }
        }
    }
}

private fun FeatureOperationEvent.isJunkCleanCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature == FeatureKey.JUNK_CLEAN &&
        action == OperationAction.CLEAN &&
        success
