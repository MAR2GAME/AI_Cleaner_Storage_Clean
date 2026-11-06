package com.aicleanerstorage.spaceclean.core.contracts.intent

interface IntentGate<I> {
    suspend fun check(request: IntentRequest<I>, ui: UiRequestPort): GateResult
}

sealed interface GateResult {
    data object Allow : GateResult
    data class Deny(val reason: String) : GateResult
    data object Busy : GateResult
    data object Cancelled : GateResult
}
