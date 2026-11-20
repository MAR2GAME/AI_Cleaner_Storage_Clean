package com.aicleanerstorage.spaceclean.base.permission

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MediaStoreDeleteRequestTest {
    private val request = MediaStoreDeleteRequest()
    private val uris = listOf("content://media/external/file/3")

    @Test
    fun invalidInputSkipsSystemRequest() = runTest {
        assertFalse(request.request(emptyList(), true) { fail() })
        assertFalse(request.request(uris, false) { fail() })
    }

    @Test
    fun systemResultDeterminesApproval() = runTest {
        for (approved in listOf(false, true)) {
            val result = async(start = CoroutineStart.UNDISPATCHED) {
                request.request(uris, true) { assertEquals(uris, it) }
            }
            assertFalse(result.isCompleted)
            request.complete(approved)
            assertEquals(approved, result.await())
        }
    }

    @Test
    fun launchFailureAllowsRetryAndLaunchCancellationPropagates() = runTest {
        assertFalse(request.request(uris, true) { throw IllegalStateException() })
        val cancellation = CancellationException("caller left")
        try {
            request.request(uris, true) { throw cancellation }
            fail()
        } catch (error: CancellationException) {
            assertSame(cancellation, error)
        }
        assertTrue(request.request(uris, true) { request.complete(true) })
    }

    @Test
    fun lateResultCannotApproveAnotherRequest() = runTest {
        val first = async(start = CoroutineStart.UNDISPATCHED) { request.request(uris, true) {} }
        first.cancel()
        first.join()
        assertTrue(first.isCancelled)
        assertFalse(request.request(uris, true) { fail() })
        request.complete(true)
        assertFalse(request.request(uris, true) { request.complete(false) })
    }

    @Test
    fun disposingCancelsTheCaller() = runTest {
        val result = async(start = CoroutineStart.UNDISPATCHED) { request.request(uris, true) {} }
        request.dispose()
        result.join()
        assertTrue(result.isCancelled)
    }
}
