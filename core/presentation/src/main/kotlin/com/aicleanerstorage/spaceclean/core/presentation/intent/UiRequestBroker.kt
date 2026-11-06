package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

class UiRequestBroker(
    private val timeoutMillis: Long = 30_000L,
) : UiRequestPort {
    private val mutex = Mutex()
    private val pending = mutableMapOf<String, CompletableDeferred<UiResult>>()
    private val requestChannel = Channel<UiRequest>(Channel.UNLIMITED)
    val requests: Flow<UiRequest> = requestChannel.receiveAsFlow()

    override suspend fun request(request: UiRequest): UiResult {
        val deferred = CompletableDeferred<UiResult>()
        mutex.withLock {
            if (pending.put(request.requestId, deferred) != null) {
                error("Duplicate UI request id: ${request.requestId}")
            }
        }
        try {
            requestChannel.send(request)
            return withTimeout(timeoutMillis) { deferred.await() }
        } catch (_: TimeoutCancellationException) {
            return UiResult.Failed
        } catch (error: CancellationException) {
            throw error
        } finally {
            mutex.withLock { pending.remove(request.requestId, deferred) }
        }
    }

    suspend fun respond(requestId: String, result: UiResult): Boolean {
        val deferred = mutex.withLock { pending[requestId] }
        return deferred?.complete(result) == true
    }

    suspend fun cancel(requestId: String): Boolean {
        val deferred = mutex.withLock { pending.remove(requestId) } ?: return false
        deferred.cancel()
        return true
    }

    suspend fun cancelAll() {
        val deferreds = mutex.withLock {
            val values = pending.values.toList()
            pending.clear()
            values
        }
        deferreds.forEach { it.cancel() }
        while (requestChannel.tryReceive().isSuccess) {
            // Drop UI requests that were queued but not rendered before disposal.
        }
    }

    fun close() {
        requestChannel.close()
    }

    companion object {
        fun newRequestId(): String = java.util.UUID.randomUUID().toString()
    }
}
