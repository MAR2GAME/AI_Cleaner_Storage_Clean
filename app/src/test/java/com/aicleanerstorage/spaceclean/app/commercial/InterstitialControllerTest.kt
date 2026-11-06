package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.core.contracts.ads.AdDriver
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene
import com.aicleanerstorage.spaceclean.core.contracts.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InterstitialControllerTest {
    private val dispatcher = StandardTestDispatcher()
    private val request = InterstitialRequest(
        scene = AdScene.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
        requestId = "clean-complete",
    )

    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `missing activity continues without starting sdk`() = runTest {
        val subject = InterstitialController(object : AdDriver {
            override suspend fun play(areaKey: String) { error("unavailable") }
            override fun isAvailable() = false
        })
        subject.show(request)
    }

    @Test fun `closed ad uses completion placement`() = runTest {
        val areas = mutableListOf<String>()
        val subject = InterstitialController(AdDriver { areas += it })
        subject.show(request)
        assertEquals(listOf("mainJunkCleanFinishAdv"), areas)
    }

    @Test fun `show exception is swallowed`() = runTest {
        var calls = 0
        val subject = InterstitialController(AdDriver { calls++; error("SDK failed") })
        subject.show(request)
        assertEquals(1, calls)
    }

    @Test fun `timeout cancels driver and returns`() = runTest {
        var cancelled = false
        val subject = InterstitialController(AdDriver {
            try { awaitCancellation() } finally { cancelled = true }
        }, timeoutMillis = 30_000L)
        val result = async { subject.show(request) }
        runCurrent()
        advanceTimeBy(29_999L)
        runCurrent()
        assertFalse(result.isCompleted)
        advanceTimeBy(1L)
        runCurrent()
        result.await()
        assertTrue(cancelled)
    }

    @Test fun `external cancellation propagates`() = runTest {
        val cancelled = CancellationException("page left")
        val subject = InterstitialController(AdDriver { throw cancelled })
        try {
            subject.show(request)
            error("Expected cancellation")
        } catch (error: CancellationException) {
            assertEquals(cancelled.message, error.message)
        }
        val waiting = InterstitialController(AdDriver { awaitCancellation() })
        try {
            withTimeout(10L) { waiting.show(request.copy(requestId = "waiting")) }
            error("Expected timeout")
        } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
            // Expected parent cancellation.
        }
    }
}
