package com.aicleanerstorage.spaceclean.app.analytics

import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsPayloadMapper
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsTracker

class AppAnalyticsReporter : AnalyticsReporter {
    override fun report(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.FeatureOperation -> AnalyticsTracker.trackFeatureOperation(event.operation)
            else -> AnalyticsPayloadMapper.map(event).takeIf { it.eventName != null }?.let {
                AnalyticsTracker.track(it.eventName!!, it.params)
            }
        }
    }
}
