package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import com.aicleanerstorage.spaceclean.common.ads.AdNeed
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanEffect
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanPhase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal suspend fun collectJunkCleanEffects(
    effects: Flow<JunkCleanEffect>,
    featureFlow: FeatureFlow,
    completionAdInterceptor: IntentInterceptor,
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
                        val result = completionAdInterceptor.intercept(AdNeed.JunkCleanCompletion)
                        currentCoroutineContext().ensureActive()
                        if (
                            result == InterceptResult.Continue && !isExiting() &&
                            currentPhase() == JunkCleanPhase.CompleteAnimation
                        ) {
                            onCompletionAdFinished()
                        }
                    }
                    featureFlow.recordOperation(effect.event)
                    completionAdJob?.start()
                } else {
                    featureFlow.handleOperation(effect.event)
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
