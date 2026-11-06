package com.aicleanerstorage.spaceclean.app.analytics

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry

data class IntentPipelineEvent(
    val name: String,
    val requestId: String,
    val source: String,
    val reason: String? = null,
)

fun interface IntentAnalyticsReporter {
    fun report(event: IntentPipelineEvent)
}

class AnalyticsIntentTelemetry<I, R>(
    private val reporter: IntentAnalyticsReporter,
    private val eventMapper: IntentAnalyticsMapper<I, R>,
) : IntentTelemetry<I, R> {
    override suspend fun onReceived(request: IntentRequest<I>) {
        report(eventMapper.event("intent_received", request, null))
    }

    override suspend fun onGateDenied(request: IntentRequest<I>, reason: String) {
        report(eventMapper.event("gate_denied", request, reason))
    }

    override suspend fun onAccepted(request: IntentRequest<I>, result: R) {
        report(eventMapper.event("business_accepted", request, null))
    }

    override suspend fun onBusinessFailed(request: IntentRequest<I>, error: Throwable) {
        report(eventMapper.event("business_failed", request, error::class.simpleName))
    }

    override suspend fun onEffectFailed(request: IntentRequest<I>, effect: String, error: Throwable) {
        report(eventMapper.event("effect_failed", request, "$effect:${error::class.simpleName}"))
    }

    private fun report(event: IntentPipelineEvent) {
        runCatching { reporter.report(event) }
    }
}

fun interface IntentAnalyticsMapper<I, R> {
    fun event(name: String, request: IntentRequest<I>, reason: String?): IntentPipelineEvent
}
