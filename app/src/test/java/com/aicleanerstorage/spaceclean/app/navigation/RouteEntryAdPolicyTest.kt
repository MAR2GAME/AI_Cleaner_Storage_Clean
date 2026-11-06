package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.app.runtime.RouteEntryAdPolicy

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import org.junit.Assert.*
import org.junit.Test

class RouteEntryAdPolicyTest {
    private val policy = RouteEntryAdPolicy()

    @Test fun `feature entry preserves feature and route identifiers`() {
        val decision = policy.entryAdDecision(AppDestination.Main().route, AppDestination.DeviceInfo.route)
        assertNotNull(decision)
        assertEquals(FeatureKey.DEVICE_INFO, decision!!.feature)
        assertEquals("device_info", decision.route)
    }

    @Test fun `home settings details and unknown targets do not request entry ads`() {
        listOf(AppDestination.Main().route, AppDestination.Settings.route,
            AppDestination.PhotosDetail().route, "unknown_route").forEach {
            assertNull(policy.entryAdDecision(AppDestination.JunkClean.route, it))
        }
    }

    @Test fun `notification alias resolves to the original typed destination`() {
        assertEquals(AppDestination.JunkClean, AppDestination.forRoute(AppDestination.normalizeNotificationRoute("/junkClean")))
    }
}
