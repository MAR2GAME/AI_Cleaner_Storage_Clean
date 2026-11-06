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
}
