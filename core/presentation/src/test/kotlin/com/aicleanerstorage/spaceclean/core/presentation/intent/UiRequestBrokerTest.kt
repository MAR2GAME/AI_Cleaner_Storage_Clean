package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiRequestBrokerTest {
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
}
