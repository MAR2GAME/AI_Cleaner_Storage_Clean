package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.security.model.VirusScanMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VirusScanDisplayTest {
    @Test
    fun quickScanShowsEachAppOnceAndSkipsPaths() =
        runTest {
            val updates = mutableListOf<VirusScanUpdate>()
            val display = startDisplay(VirusScanMode.Quick, updates)
            display.enqueueApp("first")
            display.enqueueApp("first")
            display.enqueuePath("/first")
            advanceTimeBy(3_000)
            assertEquals(listOf("first"), updates.filterIsInstance<VirusScanUpdate.AppDisplayed>().map { it.icon })
            advanceTimeBy(5_000)
            display.enqueueApp("late")
            runCurrent()
            assertEquals(1, updates.filterIsInstance<VirusScanUpdate.AppDisplayed>().size)
            assertFalse(updates.contains(VirusScanUpdate.PathDisplayStarted))
            assertEquals(0.99f, updates.filterIsInstance<VirusScanUpdate.ProgressUpdated>().last().fraction)
            display.stop()
        }

    @Test
    fun deepScanSwitchesToPathsAndStopsPendingUpdatesOnCancel() =
        runTest {
            val updates = mutableListOf<VirusScanUpdate>()
            val display = startDisplay(VirusScanMode.Deep, updates)
            display.enqueueApp("app")
            display.enqueuePath("/first")
            advanceTimeBy(10_000)
            val appIndex = updates.indexOf(VirusScanUpdate.AppDisplayed("app", "app"))
            val switchIndex = updates.indexOf(VirusScanUpdate.PathDisplayStarted)
            val pathIndex = updates.indexOf(VirusScanUpdate.PathDisplayed("/first"))
            assertTrue(appIndex >= 0 && appIndex < switchIndex && switchIndex < pathIndex)
            display.enqueuePath("/pending")
            display.stop()
            val count = updates.size
            advanceTimeBy(10_000)
            assertEquals(count, updates.size)
        }

    private fun TestScope.startDisplay(
        mode: VirusScanMode,
        updates: MutableList<VirusScanUpdate>,
    ) = VirusScanDisplay(backgroundScope, mode, { it }, { updates.add(it) }, { testScheduler.currentTime })
        .also { it.start(testScheduler.currentTime) }
}
