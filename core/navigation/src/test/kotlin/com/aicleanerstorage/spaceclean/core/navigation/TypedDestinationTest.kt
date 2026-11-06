package com.aicleanerstorage.spaceclean.core.navigation

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TypedDestinationTest {
    @Test fun `all registered destinations survive serialization with metadata intact`() {
        (AppDestination.all + AppDestination.PhotosDetail(37)).forEach { destination ->
            val restored = Json.decodeFromString<AppDestination>(Json.encodeToString(destination))
            assertEquals(destination, restored)
            assertEquals(destination.route, restored.route)
            assertEquals(destination.featureKey, restored.featureKey)
        }
    }

    @Test fun `legacy detail indexes survive conversion to typed routes`() {
        val route = AppDestination.PhotosDetail().route
        assertEquals(AppDestination.PhotosDetail(3), AppDestination.forRoute("$route/3"))
        assertEquals(AppDestination.PhotosDetail(8), AppDestination.forRoute("$route?initialIndex=8"))
        assertEquals(AppDestination.PhotosDetail(0), AppDestination.forRoute("$route/-2"))
    }

    @Test fun `notification home aliases preserve selected tab`() {
        for (tab in MainTab.entries) {
            val destination = AppDestination.Main(tab)
            destination.notificationAliases.forEach { alias ->
                assertEquals(destination, AppDestination.forRoute(AppDestination.normalizeNotificationRoute(alias)))
            }
        }
    }
}

class ToolNavigationTargetTest {
    @Test fun `tool targets map to distinct typed destinations`() {
        val destinations = ToolNavigationTarget.entries.map { it.toAppDestination() }
        assertEquals(ToolNavigationTarget.entries.size, destinations.toSet().size)
        assertTrue(destinations.contains(AppDestination.JunkClean))
        assertEquals(AppDestination.Main(MainTab.Toolbox), ToolNavigationTarget.Toolbox.toAppDestination())
    }
}
