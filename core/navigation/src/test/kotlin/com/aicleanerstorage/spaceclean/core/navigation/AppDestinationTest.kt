package com.aicleanerstorage.spaceclean.core.navigation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AppDestinationTest {
    @Test
    fun routesAndFeatureEntriesAreUnique() {
        assertEquals(AppDestination.all.size, AppDestination.all.map { it.route }.distinct().size)
        assertEquals(FeatureKey.entries.size, AppDestination.featureEntries.size)
        assertEquals(
            FeatureKey.entries.toSet(),
            AppDestination.featureEntries.mapNotNull { it.featureKey }.toSet(),
        )
    }

    @Test
    fun routeLookupDelegatesToDestinations() {
        AppDestination.all.forEach { destination ->
            assertEquals(destination, AppDestination.forRoute(destination.route))
        }
    }

    @Test
    fun destinationsOwnFeatureEntryAndNestedRouteMappings() {
        AppDestination.featureEntries.forEach { destination ->
            val feature = requireNotNull(destination.featureKey)
            assertEquals(destination.route, AppDestination.forFeature(feature)?.route)
            assertEquals(feature, AppDestination.featureForRoute(destination.route))
        }

        assertEquals(
            FeatureKey.ANTI_VIRUS,
            AppDestination.featureForRoute(AppDestination.VirusQuickScan.route),
        )
        assertEquals(
            FeatureKey.PHOTOS,
            AppDestination.featureForRoute("${AppDestination.PhotosDetail().route}/3"),
        )
    }

    @Test
    fun everyNotificationAliasResolvesToItsRegisteredDestination() {
        val mappings = AppDestination.notificationAliases()

        assertTrue(mappings.isNotEmpty())
        mappings.forEach { mapping ->
            assertEquals(mapping.route, AppDestination.normalizeNotificationRoute(mapping.rawRoute))
            assertNotNull(AppDestination.forRoute(mapping.route))
        }
        AppDestination.notificationTargetRoutes.forEach { route ->
            assertEquals(route, AppDestination.normalizeNotificationRoute(route))
        }
    }

    @Test
    fun mainDestinationsPreserveInitialTabThroughSerialization() {
        MainTab.entries.forEach { tab ->
            val destination = AppDestination.Main(tab)
            val restored = Json.decodeFromString<AppDestination>(Json.encodeToString<AppDestination>(destination))
            assertEquals(destination, restored)
            assertEquals(tab, (restored as AppDestination.Main).initialTab)
        }
    }

    @Test
    fun toolTargetsPreserveFeatureAndRouteAliasMetadata() {
        ToolNavigationTarget.entries
            .filter { it != ToolNavigationTarget.Toolbox }
            .forEach { target ->
                val destination = target.toAppDestination()
                val feature = requireNotNull(target.featureKey)
                assertEquals(feature, destination.featureKey)
                assertEquals(destination.route, AppDestination.forFeature(feature)?.route)
                destination.notificationAliases.forEach { alias ->
                    assertEquals(destination.route, AppDestination.normalizeNotificationRoute(alias))
                }
            }
    }
}
