package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal enum class NavigationKind {
    Navigate,
    ReplaceCurrent,
    ResetTo,
    TopLevel,
}

internal data class NavigationRequest(
    val requestId: Long,
    val sourceEntryId: String?,
    val destination: AppDestination,
    val kind: NavigationKind,
    val sourceRoute: String? = null,
)

internal sealed interface NavigationEvent {
    data class Accepted(val request: NavigationRequest) : NavigationEvent
    data class Cancelled(val request: NavigationRequest) : NavigationEvent
}

internal data class NavigationReceipt(
    val requestId: Long,
    val accepted: Boolean,
)

internal data class NavigationTicket(
    val requestId: Long,
    val completion: Deferred<NavigationReceipt>,
)

internal class NavigationCoordinator(
    private val scope: CoroutineScope,
) {
    private val _events = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<NavigationEvent> = _events

    private var nextRequestId = 0L
    private var pending: Pending? = null

    fun submit(
        sourceEntryId: String?,
        destination: AppDestination,
        kind: NavigationKind,
        sourceRoute: String? = null,
        action: suspend () -> Boolean,
    ): NavigationTicket {
        val existing = pending
        if (existing != null && existing.job.isActive &&
            existing.request.sourceEntryId == sourceEntryId &&
            existing.request.destination == destination &&
            existing.request.kind == kind
        ) return existing.ticket

        cancel()
        val request = NavigationRequest(++nextRequestId, sourceEntryId, destination, kind, sourceRoute)
        val completion = CompletableDeferred<NavigationReceipt>()
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                val accepted = action()
                currentCoroutineContext().ensureActive()
                completion.complete(NavigationReceipt(request.requestId, accepted))
                if (accepted) _events.tryEmit(NavigationEvent.Accepted(request))
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                completion.complete(NavigationReceipt(request.requestId, false))
            } finally {
                if (pending?.request?.requestId == request.requestId) pending = null
            }
        }
        val ticket = NavigationTicket(request.requestId, completion)
        pending = Pending(request, ticket, completion, job)
        job.invokeOnCompletion { cause ->
            if (cause != null && completion.completeExceptionally(CancellationException("Navigation cancelled"))) {
                _events.tryEmit(NavigationEvent.Cancelled(request))
            }
            if (pending?.request?.requestId == request.requestId) pending = null
        }
        job.start()
        return ticket
    }

    fun sourceChanged(currentEntryId: String?) {
        val source = pending?.request?.sourceEntryId ?: return
        if (source != currentEntryId) cancel()
    }

    fun cancelForSource(sourceEntryId: String?) {
        if (pending?.request?.sourceEntryId == sourceEntryId) cancel()
    }

    fun cancel() {
        pending?.let {
            if (it.completion.completeExceptionally(CancellationException("Navigation cancelled"))) {
                _events.tryEmit(NavigationEvent.Cancelled(it.request))
            }
            it.job.cancel()
        }
        pending = null
    }

    private data class Pending(
        val request: NavigationRequest,
        val ticket: NavigationTicket,
        val completion: CompletableDeferred<NavigationReceipt>,
        val job: Job,
    )
}
