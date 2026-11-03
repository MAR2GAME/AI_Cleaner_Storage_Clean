package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore
import org.junit.Assert.assertEquals
import org.junit.Test

class AdFeatureFlowTest {
    @Test
    fun `scan lifecycle reports each operation event without an ad`() {
        val ads = RecordingAdRunner()
        val reports = RecordingAnalyticsReporter()
        val busyStates = mutableListOf<Boolean>()
        val runtime = runtime(ads, reports, busyStates)
        var continuationCount = 0

        runtime.handleOperation(FeatureOperationEvent.ScanStarted(FeatureKey.PHOTOS)) {
            continuationCount += 1
        }
        runtime.handleOperation(FeatureOperationEvent.ScanFinished(FeatureKey.PHOTOS, hasResult = true)) {
            continuationCount += 1
        }

        assertEquals(listOf(true, false), busyStates)
        assertEquals(
            listOf(
                AnalyticsEvent.FeatureOperation(FeatureOperationEvent.ScanStarted(FeatureKey.PHOTOS)),
                AnalyticsEvent.FeatureOperation(FeatureOperationEvent.ScanFinished(FeatureKey.PHOTOS, true)),
            ),
            reports.events,
        )
        assertEquals(2, continuationCount)
        assertEquals(emptyList<AdRequest>(), ads.requests)
    }

    @Test
    fun `operation start reports once without an ad`() {
        val ads = RecordingAdRunner()
        val reports = RecordingAnalyticsReporter()
        val runtime = runtime(ads, reports)
        var continuationCount = 0
        val event = FeatureOperationEvent.OperationStarted(
            feature = FeatureKey.JUNK_CLEAN,
            action = OperationAction.CLEAN,
        )

        runtime.handleOperation(event) { continuationCount += 1 }

        assertEquals(listOf(AnalyticsEvent.FeatureOperation(event)), reports.events)
        assertEquals(emptyList<AdRequest>(), ads.requests)
        assertEquals(1, continuationCount)
    }

    @Test
    fun `successful operation reports once and preserves ad request`() {
        val ads = RecordingAdRunner()
        val reports = RecordingAnalyticsReporter()
        val runtime = runtime(ads, reports)
        var continuationCount = 0
        val event = FeatureOperationEvent.OperationFinished(
            feature = FeatureKey.JUNK_CLEAN,
            action = OperationAction.CLEAN,
            success = true,
        )

        runtime.handleOperation(event) { continuationCount += 1 }

        assertEquals(listOf(AnalyticsEvent.FeatureOperation(event)), reports.events)
        assertEquals(
            listOf(
                AdRequest(
                    scene = AdScene.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
                    requestId = "operation_finished_JUNK_CLEAN_CLEAN_true",
                ),
            ),
            ads.requests,
        )
        assertEquals(0, continuationCount)

        ads.continueRequest()
        ads.continueRequest()
        assertEquals(1, continuationCount)
    }

    @Test
    fun `failed operation reports without requesting completion ad`() {
        val ads = RecordingAdRunner()
        val reports = RecordingAnalyticsReporter()
        val runtime = runtime(ads, reports)
        var continuationCount = 0
        val event = FeatureOperationEvent.OperationFinished(
            feature = FeatureKey.JUNK_CLEAN,
            action = OperationAction.CLEAN,
            success = false,
        )

        runtime.handleOperation(event) { continuationCount += 1 }

        assertEquals(listOf(AnalyticsEvent.FeatureOperation(event)), reports.events)
        assertEquals(emptyList<AdRequest>(), ads.requests)
        assertEquals(1, continuationCount)
    }

    @Test
    fun `return and permission exits preserve scene and request id`() {
        val ads = RecordingAdRunner()
        val reports = RecordingAnalyticsReporter()
        val runtime = runtime(ads, reports)

        runtime.exit(FeatureKey.NETWORK_SPEED, FeatureExitReason.Return) {}
        runtime.exit(FeatureKey.APP_USAGE, FeatureExitReason.PermissionRejected) {}

        assertEquals(
            listOf(
                AdRequest(AdScene.ReturnHome(FeatureKey.NETWORK_SPEED), "return_home_NETWORK_SPEED"),
                AdRequest(AdScene.PermissionRejected(FeatureKey.APP_USAGE), "permission_rejected_APP_USAGE"),
            ),
            ads.requests,
        )
        assertEquals(emptyList<AnalyticsEvent>(), reports.events)
    }

    private fun runtime(
        ads: RecordingAdRunner,
        reports: RecordingAnalyticsReporter,
        busyStates: MutableList<Boolean> = mutableListOf(),
    ): AdFeatureFlow = AdFeatureFlow(
        interstitialAds = ads,
        cleanupHistory = NoopCleanupHistoryStore,
        onOperationBusyChanged = busyStates::add,
        analyticsReporter = reports,
    )

    private data class AdRequest(val scene: AdScene?, val requestId: String)

    private class RecordingAdRunner : InterstitialAdRunner {
        val requests = mutableListOf<AdRequest>()
        private var continuation: (() -> Unit)? = null

        override fun run(scene: AdScene?, requestId: String, onContinue: () -> Unit) {
            if (scene is AdScene.OperationFinished && !scene.success) {
                onContinue()
                return
            }
            requests += AdRequest(scene, requestId)
            continuation = onContinue
        }

        override fun runRouteEntry(fromRoute: String?, targetRoute: String?, onContinue: () -> Unit) = onContinue()

        fun continueRequest() {
            continuation?.invoke()
        }
    }

    private class RecordingAnalyticsReporter : AnalyticsReporter {
        val events = mutableListOf<AnalyticsEvent>()

        override fun report(event: AnalyticsEvent) {
            events += event
        }
    }

    private object NoopCleanupHistoryStore : CleanupHistoryStore {
        override fun hasCompletedCleanup(): Boolean = false
        override fun recordCompletedCleanup() = Unit
    }
}
