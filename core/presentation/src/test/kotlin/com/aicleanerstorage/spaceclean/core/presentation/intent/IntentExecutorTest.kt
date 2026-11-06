package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentExecutorTest {
    @Test
    fun deniedGateSkipsHandlerAndEffects() = runTest {
        var handled = false
        var effected = false
        val executor = IntentExecutor(
            ui = NoopUi,
            gates = listOf(object : IntentGate<String> {
                override suspend fun check(request: IntentRequest<String>, ui: UiRequestPort) =
                    GateResult.Deny("denied")
            }),
            handler = object : IntentHandler<String, Int> {
                override suspend fun handle(request: IntentRequest<String>): Int {
                    handled = true
                    return 1
                }
            },
            effects = listOf(object : IntentEffect<String, Int> {
                override suspend fun afterAccepted(request: IntentRequest<String>, result: Int, ui: UiRequestPort) {
                    effected = true
                }
            }),
        )

        assertEquals("denied", (executor.execute("intent", "test") as ExecutionResult.Rejected).reason)
        assertTrue(!handled)
        assertTrue(!effected)
        executor.close()
    }

    @Test
    fun acceptedResultIsReturnedBeforeBestEffortEffectFinishes() = runTest {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val executor = IntentExecutor(
            ui = NoopUi,
            gates = emptyList(),
            handler = object : IntentHandler<String, Int> {
                override suspend fun handle(request: IntentRequest<String>) = 2
            },
            effects = listOf(object : IntentEffect<String, Int> {
                override suspend fun afterAccepted(request: IntentRequest<String>, result: Int, ui: UiRequestPort) {
                    started.complete(Unit)
                    release.await()
                }
            }),
        )

        assertEquals(2, (executor.execute("intent", "test") as ExecutionResult.Accepted).value)
        started.await()
        release.complete(Unit)
        executor.close()
    }

    @Test
    fun handlerFailureIsReturnedWithoutStartingEffects() = runTest {
        var effected = false
        val error = IllegalStateException("business failed")
        val executor = IntentExecutor(
            ui = NoopUi,
            gates = emptyList(),
            handler = object : IntentHandler<String, Int> {
                override suspend fun handle(request: IntentRequest<String>): Int = throw error
            },
            effects = listOf(object : IntentEffect<String, Int> {
                override suspend fun afterAccepted(request: IntentRequest<String>, result: Int, ui: UiRequestPort) {
                    effected = true
                }
            }),
        )

        val result = executor.execute("intent", "test")
        assertTrue(result is ExecutionResult.Failed)
        assertTrue(!effected)
        executor.close()
    }

    private object NoopUi : UiRequestPort {
        override suspend fun request(request: com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest) =
            com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult.Failed
    }
}
