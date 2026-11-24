package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnalyticsDetailsTest {
    @Test
    fun coreFeatureRoutesKeepTheirDetails() {
        assertEquals(AnalyticsDetails.JUNK_CLEAN, AnalyticsDetails.forRoute(route(AppDestination.JunkClean)))
        assertEquals(AnalyticsDetails.VIRUS_ANTI, AnalyticsDetails.forRoute(route(AppDestination.AntiVirus)))
        assertEquals(AnalyticsDetails.APP_LOCK, AnalyticsDetails.forRoute(route(AppDestination.AppLock)))
        assertEquals(AnalyticsDetails.NETWORK_SPEED, AnalyticsDetails.forRoute(route(AppDestination.NetworkSpeed)))
    }

    @Test
    fun virusSubRoutesCountAsVirusAnti() {
        val routes =
            listOf(
                AppDestination.VirusQuickScan,
                AppDestination.VirusDeepScan,
                AppDestination.VirusResult,
                AppDestination.NoVirusResult,
            )
        routes.forEach { destination ->
            assertEquals(AnalyticsDetails.VIRUS_ANTI, AnalyticsDetails.forRoute(route(destination)))
        }
    }

    @Test
    fun detailRoutesCountAsFileManage() {
        val routes =
            listOf(
                AppDestination.PhotosDetail(0),
                AppDestination.SimilarPhotosDetail(0),
                AppDestination.ScreenshotsDetail(0),
                AppDestination.VideosDetail(0),
                AppDestination.AudiosDetail(0),
                AppDestination.LargeFilesDetail(0),
                AppDestination.DocumentsDetail(0),
            )
        routes.forEach { destination ->
            assertEquals(AnalyticsDetails.FILE_MANAGE, AnalyticsDetails.forRoute(route(destination)))
            assertEquals(AnalyticsDetails.FILE_MANAGE, AnalyticsDetails.forDestination(destination))
        }
    }

    @Test
    fun unknownAndRootRoutesHaveNoDetails() {
        assertNull(AnalyticsDetails.forRoute(null))
        assertNull(AnalyticsDetails.forRoute("com.aicleanerstorage.spaceclean.base.navigation.AppDestination.Settings"))
        assertNull(AnalyticsDetails.forRoute("com.aicleanerstorage.spaceclean.base.navigation.AppDestination.Main"))
        assertNull(AnalyticsDetails.forDestination(AppDestination.Main()))
        assertNull(AnalyticsDetails.forDestination(AppDestination.Settings))
    }

    private fun route(destination: AppDestination): String = requireNotNull(destination::class.qualifiedName) + "/0"
}
