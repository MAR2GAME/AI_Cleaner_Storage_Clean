package com.aicleanerstorage.spaceclean.app.platform.analytics

import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.app.platform.analytics.AnalyticsPayloadMapper

import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsPayloadMapperTest {
    @Test
    fun `junk exits include feature and exact reason`() {
        FeatureExitReason.entries.forEach { reason ->
            val payload = AnalyticsPayloadMapper.map(AnalyticsEvent.FeatureExit(FeatureKey.JUNK_CLEAN, reason))
            assertEquals("feature_exit", payload.eventName)
            assertEquals(mapOf("details" to "junkClean", "exit_reason" to reason.name), payload.params)
        }
    }
}

