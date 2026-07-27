package com.aicleanerstorage.spaceclean.operation

import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.app.runtime.LocalFeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureFlowTest {
    @Test
    fun `scan lifecycle updates busy state and continues immediately`() {
        val history = FakeCleanupHistoryStore()
        val busyStates = mutableListOf<Boolean>()
        val runtime = runtime(history, busyStates)
        var continuationCount = 0

        runtime.handleOperation(FeatureOperationEvent.ScanStarted(FeatureKey.PHOTOS)) {
            continuationCount += 1
        }
        runtime.handleOperation(FeatureOperationEvent.ScanFinished(FeatureKey.PHOTOS, hasResult = true)) {
            continuationCount += 1
        }

        assertEquals(listOf(true, false), busyStates)
        assertEquals(2, continuationCount)
        assertEquals(0, history.completedCount)
    }

    @Test
    fun `successful cleanup records local history and continues`() {
        val history = FakeCleanupHistoryStore()
        val runtime = runtime(history)
        var continued = false

        runtime.handleOperation(
            FeatureOperationEvent.OperationFinished(
                feature = FeatureKey.JUNK_CLEAN,
                action = OperationAction.CLEAN,
                success = true,
            ),
        ) { continued = true }
        runtime.handleOperation(
            FeatureOperationEvent.ScanFinished(
                feature = FeatureKey.ANTI_VIRUS,
                hasResult = false,
            ),
        )

        assertTrue(continued)
        assertEquals(2, history.completedCount)
    }

    @Test
    fun `exit clears busy and continues without an ad`() {
        val busyStates = mutableListOf<Boolean>()
        val runtime = runtime(FakeCleanupHistoryStore(), busyStates)
        var continued = false

        runtime.exit(FeatureKey.NETWORK_SPEED, FeatureExitReason.Return) { continued = true }

        assertEquals(listOf(false), busyStates)
        assertTrue(continued)
    }

    private fun runtime(
        history: CleanupHistoryStore,
        busyStates: MutableList<Boolean> = mutableListOf(),
    ) =
        LocalFeatureFlow(
            cleanupHistory = history,
            onOperationBusyChanged = busyStates::add,
        )

    private class FakeCleanupHistoryStore : CleanupHistoryStore {
        var completedCount = 0

        override fun hasCompletedCleanup(): Boolean = completedCount > 0

        override fun recordCompletedCleanup() {
            completedCount += 1
        }
    }
}
