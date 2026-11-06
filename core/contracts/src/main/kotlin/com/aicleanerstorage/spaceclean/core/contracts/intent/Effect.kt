package com.aicleanerstorage.spaceclean.core.contracts.intent

interface IntentEffect<I, R> {
    suspend fun afterAccepted(
        request: IntentRequest<I>,
        result: R,
        ui: UiRequestPort,
    )
}
