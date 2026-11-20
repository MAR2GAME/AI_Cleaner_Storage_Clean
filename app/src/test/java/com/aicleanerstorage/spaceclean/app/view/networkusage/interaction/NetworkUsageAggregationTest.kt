package com.aicleanerstorage.spaceclean.app.view.networkusage.interaction

import com.aicleanerstorage.networkkit.model.NetworkUsageApp
import com.aicleanerstorage.networkkit.model.TrafficSource
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkUsageAggregationTest {
    @Test
    fun unknownSourcesAndUnattributedBytesProduceOneRowAcrossBrands() {
        for (name in listOf("example.first", "example.second")) {
            val apps = listOf(
                NetworkUsageApp(TrafficSource.App(name, "App"), 20, 30),
                NetworkUsageApp(TrafficSource.SystemAndUnknown, 5, 5),
                NetworkUsageApp(TrafficSource.SystemAndUnknown, 3, 7),
            )
            val rows = buildNetworkUsageDisplayItems(apps, 90)
            assertEquals(40L, rows.single { it.isAggregate }.totalBytes)
            assertEquals(name, rows.single { !it.isAggregate }.packageName)
            assertEquals(90L, rows.sumOf { it.totalBytes })
        }
    }
}
