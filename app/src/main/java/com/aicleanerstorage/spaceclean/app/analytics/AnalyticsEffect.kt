package com.aicleanerstorage.spaceclean.app.analytics

import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort

class AnalyticsEffect<I, R>(
    private val reporter: AnalyticsReporter,
    private val event: (IntentRequest<I>, R) -> AnalyticsEvent,
) : IntentEffect<I, R> {
    override suspend fun afterAccepted(
        request: IntentRequest<I>,
        result: R,
        ui: UiRequestPort,
    ) {
        reporter.report(event(request, result))
    }
}
