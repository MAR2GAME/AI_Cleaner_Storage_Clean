package com.aicleanerstorage.spaceclean.app.analytics

import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter

class SafeAnalyticsReporter(
    private val delegate: AnalyticsReporter,
) : AnalyticsReporter {
    override fun report(event: AnalyticsEvent) {
        runCatching { delegate.report(event) }
    }
}
