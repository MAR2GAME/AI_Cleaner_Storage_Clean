package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.common.ads.*
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdQueueTest {
    private fun request(id: String) = InterstitialRequest(AdScene.ReturnHome(FeatureKey.JUNK_CLEAN), id)

    @Test fun `queue wait consumes timeout so second playback receives only remaining budget`() = runTest {
        var calls = 0
        val firstClose = CompletableDeferred<Unit>()
        val subject = AdInterceptor(AdDriver { calls++; firstClose.await() }, dispatcher = StandardTestDispatcher(testScheduler))
        val first = launch { subject.show(request("first")) }
        runCurrent()
        advanceTimeBy(1000)
        val queued = launch { subject.show(request("queued")) }
        runCurrent()
        advanceTimeBy(29_000)
        runCurrent()
        assertTrue(first.isCompleted)
        assertEquals(2, calls)
        advanceTimeBy(999)
        runCurrent()
        assertFalse(queued.isCompleted)
        advanceTimeBy(1)
        runCurrent()
        assertTrue(queued.isCompleted)
    }

    @Test fun `duplicate id completes without cancelling owner and queued cancellation releases id`() = runTest {
        var calls = 0
        val closed = CompletableDeferred<Unit>()
        val subject = AdInterceptor(AdDriver { calls++; closed.await() }, dispatcher = StandardTestDispatcher(testScheduler))
        val first = launch { subject.show(request("first")) }
        runCurrent()
        subject.show(request("first"))
        assertEquals(1, calls)
        assertTrue(first.isActive)
        val queued = launch { subject.show(request("second")) }
        runCurrent()
        queued.cancelAndJoin()
        val retry = launch { subject.show(request("second")) }
        runCurrent()
        assertEquals(1, calls)
        closed.complete(Unit)
        runCurrent()
        assertEquals(2, calls)
        assertTrue(retry.isCompleted)
    }

    @Test fun `cancel current releases busy and queue rechecks eligibility`() = runTest {
        var allowed = true
        var calls = 0
        val busy = mutableListOf<Boolean>()
        val subject = AdInterceptor(AdDriver { calls++; awaitCancellation() },
            canShow = { allowed }, onBusyChanged = busy::add, dispatcher = StandardTestDispatcher(testScheduler))
        val first = launch { subject.show(request("first")) }
        runCurrent()
        val second = launch { subject.show(request("second")) }
        runCurrent()
        allowed = false
        first.cancelAndJoin()
        runCurrent()
        assertTrue(second.isCompleted)
        assertEquals(1, calls)
        assertEquals(listOf(true, false), busy)
        allowed = true
        val retry = launch { subject.show(request("first")) }
        runCurrent()
        assertEquals(2, calls)
        retry.cancelAndJoin()
    }

    @Test fun `dispose cancels owned waits without cancelling parent and startup exception releases id`() = runTest {
        var calls = 0
        val subject = AdInterceptor(AdDriver { calls++; if (calls == 1) error("startup") else awaitCancellation() },
            dispatcher = StandardTestDispatcher(testScheduler))
        subject.show(request("same"))
        val current = launch { subject.show(request("same")) }
        val queued = launch { subject.show(request("queued")) }
        runCurrent()
        subject.dispose()
        runCurrent()
        assertEquals(2, calls)
        assertTrue(current.isCancelled)
        assertTrue(queued.isCancelled)
        assertTrue(currentCoroutineContext().isActive)
    }
}
