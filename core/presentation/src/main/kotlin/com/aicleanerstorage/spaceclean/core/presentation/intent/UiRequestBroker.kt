package com.aicleanerstorage.spaceclean.core.presentation.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

class UiRequestBroker(
    private val timeoutMillis: Long = 30_000L,
) : UiRequestPort {
    private val mutex = Mutex()
    private val pending = mutableMapOf<String, CompletableDeferred<UiResult>>()
    private val _activeIds = MutableStateFlow<Set<String>>(emptySet())
    val activeIds = _activeIds.asStateFlow()
    private val requestChannel = Channel<UiRequest>(Channel.UNLIMITED)
    val requests: Flow<UiRequest> = requestChannel.receiveAsFlow()

    override suspend fun request(request: UiRequest): UiResult {
        val deferred = CompletableDeferred<UiResult>()
        try {
            mutex.withLock {
                check(request.requestId !in pending) { "Duplicate UI request id: ${request.requestId}" }
                pending[request.requestId] = deferred
                _activeIds.value = pending.keys.toSet()
                requestChannel.trySend(request).getOrThrow()
            }
            return withTimeoutOrNull(timeoutMillis) { deferred.await() } ?: UiResult.Failed
        } catch (error: CancellationException) {
            throw error
        } finally {
            withContext(NonCancellable) {
                mutex.withLock {
                    pending.remove(request.requestId, deferred)
                    _activeIds.value = pending.keys.toSet()
                }
            }
        }
    }

    suspend fun respond(requestId: String, result: UiResult): Boolean {
        return mutex.withLock {
            val deferred = pending.remove(requestId) ?: return@withLock false
            _activeIds.value = pending.keys.toSet()
            deferred.complete(result)
        }
    }

    suspend fun cancel(requestId: String): Boolean {
        val deferred = mutex.withLock {
            pending.remove(requestId).also { _activeIds.value = pending.keys.toSet() }
        } ?: return false
        deferred.cancel()
        return true
    }

    suspend fun cancelAll() {
        val deferreds = mutex.withLock {
            val values = pending.values.toList()
            pending.clear()
            _activeIds.value = emptySet()
            while (requestChannel.tryReceive().isSuccess) {
                // Discard only the requests belonging to the disposed Host.
            }
            values
        }
        deferreds.forEach { it.cancel() }
    }

    suspend fun pendingRequestCount(): Int = mutex.withLock { pending.size }

    fun close() {
        requestChannel.close()
    }

    companion object {
        fun newRequestId(): String = java.util.UUID.randomUUID().toString()
    }
}
