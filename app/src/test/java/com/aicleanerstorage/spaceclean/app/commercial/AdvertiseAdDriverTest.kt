package com.aicleanerstorage.spaceclean.app.commercial

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdvertiseAdDriverTest {
    @Test fun `synchronous and duplicate sdk close resume once`() = runTest {
        var continuations = 0
        awaitAdClose { close -> close(); close() }
        continuations++
        assertEquals(1, continuations)
    }
    @Test fun `late sdk close after cancellation is ignored`() = runTest {
        lateinit var close: () -> Unit
        var continued = false
        val job = launch { awaitAdClose { close = it }; continued = true }
        runCurrent()
        job.cancelAndJoin()
        close()
        close()
        assertFalse(continued)
    }
    @Test fun `startup failure propagates to interceptor`() = runTest {
        try { awaitAdClose { error("SDK startup failure") }; fail("Expected failure") }
        catch (error: IllegalStateException) { assertEquals("SDK startup failure", error.message) }
    }
}
