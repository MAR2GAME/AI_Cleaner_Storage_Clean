package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlin.coroutines.coroutineContext

interface IntentHandler<I, R> {
    suspend fun handle(request: IntentRequest<I>): R
}

class IntentExecutor<I, R>(
    private val ui: UiRequestPort,
    private val gates: List<IntentGate<I>>,
    private val handler: IntentHandler<I, R>,
    private val effects: List<IntentEffect<I, R>> = emptyList(),
    private val telemetry: IntentTelemetry<I, R>? = null,
    effectScope: CoroutineScope? = null,
) : AutoCloseable {
    private val ownsEffectScope = effectScope == null
    private val effectScope = effectScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Default)

    suspend fun execute(intent: I, source: String): ExecutionResult<R> {
        val request = IntentRequest(newIntentRequestId(), source, intent)
        return try {
            runTelemetry { telemetry?.onReceived(request) }
            for (gate in gates) {
                coroutineContext.ensureActive()
                when (val decision = gate.check(request, ui)) {
                    GateResult.Allow -> Unit
                    is GateResult.Deny -> {
                        runTelemetry { telemetry?.onGateDenied(request, decision.reason) }
                        return ExecutionResult.Rejected(request.id, decision.reason)
                    }
                    GateResult.Busy -> return ExecutionResult.Busy(request.id)
                    GateResult.Cancelled -> return ExecutionResult.Cancelled(request.id)
                }
            }
            val value = try {
                handler.handle(request)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                runTelemetry { telemetry?.onBusinessFailed(request, error) }
                return ExecutionResult.Failed(request.id, error)
            }
            runTelemetry { telemetry?.onAccepted(request, value) }
            effects.forEach { effect ->
                effectScope.launch {
                    try {
                        effect.afterAccepted(request, value, ui)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Throwable) {
                        runTelemetry { telemetry?.onEffectFailed(request, effect::class.simpleName ?: "effect", error) }
                    }
                }
            }
            ExecutionResult.Accepted(request.id, value)
        } catch (error: CancellationException) {
            throw error
        }
    }

    private suspend fun runTelemetry(block: suspend () -> Unit) {
        try {
            block()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            // Telemetry is best effort and cannot change business execution.
        }
    }

    override fun close() {
        if (ownsEffectScope) effectScope.cancel()
    }
}
