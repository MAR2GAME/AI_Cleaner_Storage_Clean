package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.supervisorScope
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiRequestBrokerTest {
    @Test
    fun callerTimeoutIsNotConvertedToBusinessFailure() = runTest {
        val broker = UiRequestBroker(timeoutMillis = 1_000)
        var returned = false
        val result = runCatching {
            withTimeout(10) {
                broker.request(UiRequest.Confirm("outer-timeout", "Title", "Message"))
                returned = true
            }
        }
        assertTrue(result.exceptionOrNull() is kotlinx.coroutines.TimeoutCancellationException)
        assertFalse(returned)
        assertTrue(broker.activeIds.value.isEmpty())
        assertFalse(broker.respond("outer-timeout", UiResult.Confirmed))
    }

    @Test
    fun hostDisposalDoesNotCloseApplicationBroker() = runTest {
        val broker = UiRequestBroker()
        val old = async { broker.request(UiRequest.Confirm("old-host", "Title", "Message")) }
        broker.requests.first()
        broker.cancelAll()
        assertTrue(runCatching { old.await() }.isFailure)
        val next = async { broker.request(UiRequest.Confirm("new-host", "Title", "Message")) }
        assertEquals("new-host", broker.requests.first().requestId)
        assertTrue(broker.respond("new-host", UiResult.Confirmed))
        assertEquals(UiResult.Confirmed, next.await())
    }

    @Test
    fun responseCompletesMatchingRequestAndDuplicateResponseIsIgnored() = runTest {
        val broker = UiRequestBroker()
        val request = UiRequest.Confirm("request-1", "Title", "Message")
        val result = async { broker.request(request) }

        withTimeout(1_000) { broker.requests.first() }
        assertTrue(broker.respond(request.requestId, UiResult.Confirmed))
        assertFalse(broker.respond(request.requestId, UiResult.Denied))
        assertTrue(result.await() === UiResult.Confirmed)
    }

    @Test
    fun cancelledRequestRejectsLateResponse() = runTest {
        val broker = UiRequestBroker()
        val request = UiRequest.Confirm("request-2", "Title", "Message")
        val result = async { broker.request(request) }

        withTimeout(1_000) { broker.requests.first() }
        broker.cancel(request.requestId)
        runCatching { result.await() }
        assertFalse(broker.respond(request.requestId, UiResult.Confirmed))
    }

    @Test
    fun timeoutRemovesPendingRequestAndRejectsLateResponse() = runTest {
        val broker = UiRequestBroker(timeoutMillis = 10)
        val request = UiRequest.Confirm("request-timeout", "Title", "Message")
        val result = async { broker.request(request) }

        withTimeout(1_000) { broker.requests.first() }
        assertTrue(result.await() === UiResult.Failed)
        assertEquals(0, broker.pendingRequestCount())
        assertFalse(broker.respond(request.requestId, UiResult.Confirmed))
    }

    @Test
    fun cancelAllCompletesAllPendingRequestsAndRejectsLateResponses() = runTest {
        val broker = UiRequestBroker()
        val first = UiRequest.Confirm("request-a", "Title", "Message")
        val second = UiRequest.Confirm("request-b", "Title", "Message")
        val firstResult = async { broker.request(first) }
        val secondResult = async { broker.request(second) }

        withTimeout(1_000) { broker.requests.first() }
        broker.cancelAll()
        runCatching { firstResult.await() }
        runCatching { secondResult.await() }
        assertEquals(0, broker.pendingRequestCount())
        assertFalse(broker.respond(first.requestId, UiResult.Confirmed))
        assertFalse(broker.respond(second.requestId, UiResult.Confirmed))
    }

    @Test
    fun callerCancellationRemovesPendingAndRejectsLateResponse() = runTest {
        val broker = UiRequestBroker()
        val result = async { broker.request(UiRequest.MediaStoreDelete("delete", listOf("content://media/1"))) }
        broker.requests.first()
        result.cancelAndJoin()
        assertEquals(emptySet<String>(), broker.activeIds.value)
        assertEquals(0, broker.pendingRequestCount())
        assertFalse(broker.respond("delete", UiResult.Confirmed))
    }

    @Test
    fun duplicateIdDoesNotReplaceOriginalWaiter() = runTest {
        supervisorScope {
            val broker = UiRequestBroker()
            val request = UiRequest.MediaStoreDelete("delete", listOf("content://media/1"))
            val first = async { broker.request(request) }
            broker.requests.first()
            val duplicate = async { broker.request(request) }
            assertTrue(runCatching { duplicate.await() }.isFailure)
            assertTrue(broker.respond("delete", UiResult.Confirmed))
            assertEquals(UiResult.Confirmed, first.await())
            assertFalse(broker.respond("delete", UiResult.Confirmed))
        }
    }
}
