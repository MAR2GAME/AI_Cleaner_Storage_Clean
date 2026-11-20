package com.aicleanerstorage.spaceclean.base.util

import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CoroutineUtilTest {
    @Test
    fun `run suspend catching returns ordinary failures`() = runTest {
        val failure = IllegalStateException("read failed")

        val result = runSuspendCatching<Unit> { throw failure }

        assertSame(failure, result.exceptionOrNull())
    }

    @Test
    fun `run suspend catching never converts cancellation into failure`() = runTest {
        try {
            runSuspendCatching<Unit> { throw CancellationException("cancelled") }
            fail("CancellationException must be rethrown")
        } catch (error: CancellationException) {
            assertEquals("cancelled", error.message)
        }
    }
}
