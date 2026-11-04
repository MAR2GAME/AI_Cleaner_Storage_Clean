package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.common.ads.*
import com.aicleanerstorage.spaceclean.common.analytics.*
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.*
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppFeatureFlowTest {
    private val reports = mutableListOf<AnalyticsEvent>()
    private val busy = mutableListOf<Boolean>()
    private var histories = 0
    private val requests = mutableListOf<InterstitialRequest>()
    private fun subject(wait: suspend () -> Unit = {}) = AppFeatureFlow(
        showAd = { requests += it; wait() },
        cleanupHistory = object : CleanupHistoryStore {
            override fun hasCompletedCleanup() = histories > 0
            override fun recordCompletedCleanup() { histories++ }
        },
        onOperationBusyChanged = busy::add,
        analyticsReporter = object : AnalyticsReporter { override fun report(event: AnalyticsEvent) { reports += event } },
    )
    private val event = FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true)

    @Test fun `record operation writes history and analytics without requesting advertisement`() = runTest {
        subject().recordOperation(event)
        assertEquals(1, histories)
        assertEquals(listOf(false), busy)
        assertEquals(listOf(AnalyticsEvent.FeatureOperation(event)), reports)
        assertTrue(requests.isEmpty())
    }

    @Test fun `operation completion preserves placement id and records only once while waiting`() = runTest {
        val closed = CompletableDeferred<Unit>()
        var continued = 0
        val job = launch { subject { closed.await() }.handleOperation(event); continued++ }
        runCurrent()
        assertEquals(1, histories)
        assertEquals(listOf(InterstitialRequest(AdScene.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
            "operation_finished_JUNK_CLEAN_CLEAN_true")), requests)
        assertEquals(0, continued)
        closed.complete(Unit)
        job.join()
        assertEquals(1, continued)
        assertEquals(1, reports.size)
    }

    @Test fun `scan lifecycle and failed completion retain recording without display placement`() = runTest {
        val flow = subject()
        flow.handleOperation(FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN))
        flow.handleOperation(FeatureOperationEvent.ScanFinished(FeatureKey.JUNK_CLEAN, true))
        flow.handleOperation(event.copy(success = false))
        assertEquals(listOf(true, false, false), busy)
        assertEquals(0, histories)
        assertEquals(3, reports.size)
        assertTrue(requests.all { AdPlacementRegistry.interstitialArea(it.scene) == null })
    }

    @Test fun `exit records reason and retains original request ids`() = runTest {
        val flow = subject()
        flow.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.Return)
        flow.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.PermissionRejected)
        assertEquals(listOf("return_home_JUNK_CLEAN", "permission_rejected_JUNK_CLEAN"), requests.map { it.requestId })
        assertEquals(listOf(AdScene.ReturnHome(FeatureKey.JUNK_CLEAN), AdScene.PermissionRejected(FeatureKey.JUNK_CLEAN)), requests.map { it.scene })
        assertEquals(listOf(AnalyticsEvent.FeatureExit(FeatureKey.JUNK_CLEAN, FeatureExitReason.Return),
            AnalyticsEvent.FeatureExit(FeatureKey.JUNK_CLEAN, FeatureExitReason.PermissionRejected)), reports)
    }

    @Test fun `page cancellation cannot navigate on late ad completion`() = runTest {
        var navigations = 0
        val closed = CompletableDeferred<Unit>()
        val job = launch {
            subject { withContext(NonCancellable) { closed.await() } }.exit(FeatureKey.JUNK_CLEAN, FeatureExitReason.Return)
            navigations++
        }
        runCurrent()
        job.cancel()
        closed.complete(Unit)
        job.join()
        assertEquals(0, navigations)
        assertEquals(1, reports.size)
    }
}
