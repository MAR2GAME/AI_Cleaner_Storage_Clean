package com.aicleanerstorage.spaceclean.common.analytics

import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
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
