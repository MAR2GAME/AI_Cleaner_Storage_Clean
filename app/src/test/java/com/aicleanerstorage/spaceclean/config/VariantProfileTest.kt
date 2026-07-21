package com.aicleanerstorage.spaceclean.config

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination

import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureCatalog
import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class VariantProfileTest {
    @Test
    fun featureCatalogMapsNestedRoutesToOwningFeature() {
        assertEquals(FeatureKey.ANTI_VIRUS, FeatureCatalog.featureForRoute(AppDestination.VirusQuickScan.route))
        assertEquals(FeatureKey.NETWORK_SCAN, FeatureCatalog.featureForRoute(AppDestination.NetworkScanDevices.route))
    }

    @Test
    fun featureCatalogDoesNotCarryAdPlacementState() {
        FeatureCatalog.specs.forEach { spec ->
            assertFalse(spec.toString().contains("Ad", ignoreCase = true))
        }
    }

    @Test
    fun appDestinationEncodesQueryArgs() {
        val route = AppDestination.Settings.withArgs(mapOf("name" to "a b&c", "path" to "/a/b"))

        assertEquals("settings?name=a+b%26c&path=%2Fa%2Fb", route)
    }

    @Test
    fun productIdentityIsGeneratedIntoBuildConfig() {
        assertEquals("ai_cleaner_storage_clean", BuildConfig.PRODUCT_PROFILE_KEY)
        assertEquals("storage_clean", BuildConfig.PRODUCT_THEME_KEY)
        assertEquals("https://sites.google.com/view/aicleaner-storageclean/home", BuildConfig.TERMS_OF_SERVICE_URL)
        assertEquals("https://sites.google.com/view/aicleanerstorageclean/home", BuildConfig.PRIVACY_POLICY_URL)
    }
}
