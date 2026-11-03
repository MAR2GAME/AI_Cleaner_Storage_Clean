package com.aicleanerstorage.spaceclean.common.presentation

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MviViewModelContractTest {
    private val policy = object : IntentPolicy {
        override val priority: Int = 100
    }

    @Test
    fun `not handled interceptor can be followed by continue`() = runTest {
        val subject = Subject(
            listOf(
                interceptor { InterceptResult.NotHandled },
                interceptor { InterceptResult.Continue },
            ),
        )

        assertEquals(InterceptResult.Continue, subject.resolve(policy))
    }

    @Test
    fun `block and busy stop policy resolution`() = runTest {
        val block = Subject(listOf(interceptor { InterceptResult.Block }))
        val busy = Subject(listOf(interceptor { InterceptResult.Busy }))

        assertEquals(InterceptResult.Block, block.resolve(policy))
        assertEquals(InterceptResult.Busy, busy.resolve(policy))
    }

    @Test
    fun `missing policy handler blocks`() = runTest {
        assertEquals(InterceptResult.Block, Subject(emptyList()).resolve(policy))
    }

    private class Subject(
        interceptors: List<IntentInterceptor>,
    ) : MviViewModel<Unit, Unit, Unit>(Unit, interceptors) {
        override fun onIntent(intent: Unit) = Unit

        suspend fun resolve(policy: IntentPolicy): InterceptResult =
            resolvePolicies(policy)
    }

    private fun interceptor(
        result: () -> InterceptResult,
    ) = object : IntentInterceptor {
        override suspend fun intercept(policy: IntentPolicy): InterceptResult = result()
    }
}
