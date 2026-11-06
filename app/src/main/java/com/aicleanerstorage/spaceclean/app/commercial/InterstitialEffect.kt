package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort

class InterstitialEffect<I, R>(
    private val placement: (IntentRequest<I>, R) -> String?,
) : IntentEffect<I, R> {
    override suspend fun afterAccepted(
        request: IntentRequest<I>,
        result: R,
        ui: UiRequestPort,
    ) {
        placement(request, result)?.let { area ->
            ui.request(UiRequest.ShowInterstitial(request.id, area))
        }
    }
}
