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
        for (result in listOf(InterceptResult.Block, InterceptResult.Busy)) {
            var handledCalls = 0
            var subsequentCalls = 0
            val subject = Subject(
                listOf(
                    interceptor {
                        handledCalls += 1
                        result
                    },
                    interceptor {
                        subsequentCalls += 1
                        InterceptResult.Continue
                    },
                ),
            )
            val nextPolicy = object : IntentPolicy {
                override val priority: Int = policy.priority + 1
            }

            assertEquals(result, subject.resolve(policy, nextPolicy))
            assertEquals(1, handledCalls)
            assertEquals(0, subsequentCalls)
        }
    }

    @Test
    fun `missing policy handler blocks`() = runTest {
        assertEquals(InterceptResult.Block, Subject(emptyList()).resolve(policy))
        val unhandled = Subject(listOf(interceptor { InterceptResult.NotHandled }))
        assertEquals(InterceptResult.Block, unhandled.resolve(policy))
    }

    private class Subject(
        interceptors: List<IntentInterceptor>,
    ) : MviViewModel<Unit, Unit, Unit>(Unit, interceptors) {
        override fun onIntent(intent: Unit) = Unit

        suspend fun resolve(vararg policies: IntentPolicy): InterceptResult =
            resolvePolicies(*policies)
    }

    private fun interceptor(
        result: () -> InterceptResult,
    ) = object : IntentInterceptor {
        override suspend fun intercept(policy: IntentPolicy): InterceptResult = result()
    }
}
