package com.aicleanerstorage.spaceclean.config

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class VariantProfileTest {
    @Test
    fun destinationsMapNestedRoutesToOwningFeature() {
        assertEquals(FeatureKey.ANTI_VIRUS, AppDestination.featureForRoute(AppDestination.VirusQuickScan.route))
        assertEquals(FeatureKey.NETWORK_SCAN, AppDestination.featureForRoute(AppDestination.NetworkScanDevices.route))
    }

    @Test
    fun destinationFeatureEntriesDoNotCarryAdPlacementState() {
        AppDestination.featureEntries.forEach { destination ->
            assertFalse(destination.toString().contains("Ad", ignoreCase = true))
        }
    }

    @Test
    fun productIdentityIsGeneratedIntoBuildConfig() {
        assertEquals("ai_cleaner_storage_clean", BuildConfig.PRODUCT_PROFILE_KEY)
        assertEquals("storage_clean", BuildConfig.PRODUCT_THEME_KEY)
        assertEquals("https://sites.google.com/view/aicleaner-storageclean/home", BuildConfig.TERMS_OF_SERVICE_URL)
        assertEquals("https://sites.google.com/view/aicleanerstorageclean/home", BuildConfig.PRIVACY_POLICY_URL)
    }
}
