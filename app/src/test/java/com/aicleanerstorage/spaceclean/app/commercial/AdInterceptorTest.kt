package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.common.ads.AdDriver
import com.aicleanerstorage.spaceclean.common.ads.AdNeed
import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdInterceptorTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun teardown() = Dispatchers.resetMain()
    @Test
    fun `missing activity continues without starting sdk`() = runTest {
        val subject = AdInterceptor(object : AdDriver {
            override suspend fun play(areaKey: String) { fail("Unavailable Activity must not play") }
            override fun isAvailable() = false
        })
        assertEquals(InterceptResult.Continue, subject.intercept(AdNeed.JunkCleanCompletion))
    }

    @Test
    fun `closed ad continues with existing completion placement and request id`() = runTest {
        val requests = mutableListOf<String>()
        val subject = AdInterceptor(AdDriver { requests += it })
        assertEquals(InterceptResult.Continue, subject.intercept(AdNeed.JunkCleanCompletion))
        assertEquals(
            listOf(com.aicleanerstorage.spaceclean.common.ads.AdPlacementRegistry.interstitialArea(
                AdScene.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
            )),
            requests,
        )
    }

    @Test
    fun `ordinary show exception continues and unrelated policy never calls driver`() = runTest {
        var calls = 0
        val subject = AdInterceptor(AdDriver { calls += 1; error("SDK failed") })
        assertEquals(InterceptResult.NotHandled, subject.intercept(PermissionNeed.StorageFiles))
        assertEquals(0, calls)
        assertEquals(InterceptResult.Continue, subject.intercept(AdNeed.JunkCleanCompletion))
        assertEquals(1, calls)
    }

    @Test
    fun `default thirty second timeout cancels driver and continues`() = runTest {
        var cancelled = false
        val subject = AdInterceptor(AdDriver {
            try { awaitCancellation() } finally { cancelled = true }
        })
        val result = async { subject.intercept(AdNeed.JunkCleanCompletion) }
        runCurrent()
        advanceTimeBy(29_999L)
        runCurrent()
        assertFalse(result.isCompleted)
        advanceTimeBy(1L)
        runCurrent()
        assertEquals(InterceptResult.Continue, result.await())
        assertTrue(cancelled)
    }

    @Test
    fun `external cancellation and outer timeout propagate`() = runTest {
        val cancelled = CancellationException("page left")
        val throwing = AdInterceptor(AdDriver { throw cancelled })
        try {
            throwing.intercept(AdNeed.JunkCleanCompletion)
            fail("Expected cancellation")
        } catch (error: CancellationException) {
            assertSame(cancelled, error)
        }
        val waiting = AdInterceptor(AdDriver { awaitCancellation() })
        try {
            withTimeout(10L) { waiting.intercept(AdNeed.JunkCleanCompletion) }
            fail("Outer timeout must not become Continue")
        } catch (_: TimeoutCancellationException) {
            // Expected parent cancellation.
        }
    }
}
