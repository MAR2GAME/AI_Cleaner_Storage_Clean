package com.aicleanerstorage.spaceclean.core.contracts.intent

interface IntentTelemetry<I, R> {
    suspend fun onReceived(request: IntentRequest<I>) = Unit
    suspend fun onGateDenied(request: IntentRequest<I>, reason: String) = Unit
    suspend fun onAccepted(request: IntentRequest<I>, result: R) = Unit
    suspend fun onBusinessFailed(request: IntentRequest<I>, error: Throwable) = Unit
    suspend fun onEffectFailed(request: IntentRequest<I>, effect: String, error: Throwable) = Unit
}

