package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
import com.aicleanerstorage.spaceclean.common.presentation.InterceptResult
import com.aicleanerstorage.spaceclean.common.presentation.IntentPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class PermissionInterceptorTest {
    @Test
    fun `permission outcomes map to continue block and busy`() = runTest {
        val outcomes = listOf(
            PermissionResult.Granted to InterceptResult.Continue,
            PermissionResult.Denied to InterceptResult.Block,
            PermissionResult.Busy to InterceptResult.Busy,
        )
        for ((permissionResult, expected) in outcomes) {
            val requests = mutableListOf<PermissionNeed>()
            val interceptor = PermissionInterceptor(gateway { need ->
                requests += need
                permissionResult
            })

            assertEquals(expected, interceptor.intercept(PermissionNeed.StorageFiles))
            assertEquals(listOf(PermissionNeed.StorageFiles), requests)
        }
    }

    @Test
    fun `unrelated policy is not handled and never requests permission`() = runTest {
        var calls = 0
        val interceptor = PermissionInterceptor(gateway {
            calls += 1
            PermissionResult.Granted
        })
        val policy = object : IntentPolicy {
            override val priority: Int = 0
        }

        assertEquals(InterceptResult.NotHandled, interceptor.intercept(policy))
        assertEquals(0, calls)
    }

    @Test
    fun `permission cancellation propagates without becoming a policy result`() = runTest {
        val cancellation = CancellationException("permission request cancelled")
        val interceptor = PermissionInterceptor(gateway { throw cancellation })

        try {
            interceptor.intercept(PermissionNeed.StorageFiles)
            fail("Expected permission cancellation to propagate")
        } catch (error: CancellationException) {
            assertSame(cancellation, error)
        }
    }

    private fun gateway(
        answer: suspend (PermissionNeed) -> PermissionResult,
    ) = object : PermissionGateway {
        override suspend fun ensure(need: PermissionNeed): PermissionResult = answer(need)
    }
}
