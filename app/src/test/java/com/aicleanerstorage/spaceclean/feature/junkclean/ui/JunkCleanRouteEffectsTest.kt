package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanEffect
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanPhase

import com.aicleanerstorage.spaceclean.app.commercial.AdInterceptor
import com.aicleanerstorage.spaceclean.common.ads.AdDriver
import com.aicleanerstorage.spaceclean.common.feature.*
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JunkCleanRouteEffectsTest {
    private val completion = FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true)

    @Test
    fun `completion records once shows once and only then enters results`() = runTest {
        val closed = CompletableDeferred<Unit>()
        var shows = 0
        val route = RouteHarness()
        route.start(this, AdInterceptor(AdDriver { shows += 1; closed.await() }))
        route.send(JunkCleanEffect.Operation(completion))
        route.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        assertEquals(listOf(completion), route.records)
        assertTrue(route.handled.isEmpty())
        assertEquals(1, shows)
        assertEquals(0, route.results)
        closed.complete(Unit)
        runCurrent()
        assertEquals(1, route.results)
        assertEquals(JunkCleanPhase.Complete, route.phase)
        route.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        assertEquals(1, shows)
        assertEquals(1, route.records.size)
    }

    @Test
    fun `exit is consumed while ad waits and cancels ad before exit flow`() = runTest {
        var cancelled = false
        val route = RouteHarness()
        route.onExitFlow = { assertTrue(cancelled) }
        route.start(this, AdInterceptor(AdDriver {
            suspendCancellableCoroutine<Unit> { continuation ->
                continuation.invokeOnCancellation { cancelled = true }
            }
        }))
        route.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        route.send(JunkCleanEffect.Exit(FeatureExitReason.Return))
        route.send(JunkCleanEffect.Exit(FeatureExitReason.Return))
        runCurrent()
        assertEquals(1, route.exits)
        assertEquals(0, route.results)
        assertTrue(route.exiting)
    }

    @Test
    fun `timeout continues to result and disposal never sends completion`() = runTest {
        val timed = RouteHarness()
        timed.start(this, AdInterceptor(AdDriver { awaitCancellation() }, timeoutMillis = 100L))
        timed.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        advanceTimeBy(100L)
        runCurrent()
        assertEquals(1, timed.results)

        val disposed = RouteHarness()
        var cancelled = false
        val job = disposed.start(this, AdInterceptor(AdDriver {
            try { awaitCancellation() } finally { cancelled = true }
        }))
        disposed.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        job.cancelAndJoin()
        assertTrue(cancelled)
        assertEquals(0, disposed.results)
    }

    @Test
    fun `late non cooperative driver return after exit cannot enter results`() = runTest {
        val closed = CompletableDeferred<Unit>()
        val route = RouteHarness()
        route.start(this, AdInterceptor(AdDriver { withContext(NonCancellable) { closed.await() } }))
        route.send(JunkCleanEffect.Operation(completion))
        runCurrent()
        route.send(JunkCleanEffect.Exit(FeatureExitReason.Return))
        runCurrent()
        assertEquals(1, route.exits)
        closed.complete(Unit)
        runCurrent()
        assertEquals(0, route.results)
    }

    @Test
    fun `completion outside animation is ignored and other operations keep existing path`() = runTest {
        val route = RouteHarness()
        route.phase = JunkCleanPhase.Preview
        var shows = 0
        route.start(this, AdInterceptor(AdDriver { shows += 1 }))
        val started = FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN)
        val failed = FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, false)
        route.send(JunkCleanEffect.Operation(completion))
        route.send(JunkCleanEffect.Operation(started))
        route.send(JunkCleanEffect.Operation(failed))
        runCurrent()
        assertTrue(route.records.isEmpty())
        assertEquals(listOf(started, failed), route.handled)
        assertEquals(0, shows)
        assertEquals(0, route.results)
    }

    @Test
    fun `delete authorization executes only while awaiting and before exit`() = runTest {
        val route = RouteHarness()
        route.start(this, AdInterceptor(AdDriver { fail("Unexpected ad") }))
        val request = JunkCleanEffect.RequestDeleteAuthorization(listOf("content://media/external/file/3"))
        route.send(request)
        runCurrent()
        assertTrue(route.authorizations.isEmpty())
        route.phase = JunkCleanPhase.AwaitingAuthorization
        route.send(request)
        runCurrent()
        assertEquals(listOf(request), route.authorizations)
        route.phase = JunkCleanPhase.Cleaning
        route.send(request)
        runCurrent()
        assertEquals(1, route.authorizations.size)
        route.phase = JunkCleanPhase.AwaitingAuthorization
        route.send(JunkCleanEffect.Exit(FeatureExitReason.Return))
        route.send(request)
        runCurrent()
        assertEquals(1, route.authorizations.size)
    }

    private class RouteHarness {
        val records = mutableListOf<FeatureOperationEvent>()
        val authorizations = mutableListOf<JunkCleanEffect.RequestDeleteAuthorization>()
        val handled = mutableListOf<FeatureOperationEvent>()
        var phase: JunkCleanPhase = JunkCleanPhase.CompleteAnimation
        var exiting = false
        var results = 0
        var exits = 0
        var onExitFlow: () -> Unit = {}
        private val effects = Channel<JunkCleanEffect>(Channel.UNLIMITED)
        private val flow = object : FeatureFlow {
            override fun recordOperation(event: FeatureOperationEvent) { records += event }
            override fun handleOperation(event: FeatureOperationEvent, onContinue: () -> Unit) {
                handled += event
                onContinue()
            }
            override fun exit(feature: FeatureKey, reason: FeatureExitReason, onContinue: () -> Unit) {
                onExitFlow()
                exits += 1
                onContinue()
            }
        }
        fun send(effect: JunkCleanEffect) { check(effects.trySend(effect).isSuccess) }
        fun start(scope: TestScope, interceptor: IntentInterceptor) =
            scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
                collectJunkCleanEffects(
                    effects = effects.receiveAsFlow(),
                    featureFlow = flow,
                    completionAdInterceptor = interceptor,
                    currentPhase = { phase },
                    isExiting = { exiting },
                    onCompletionAdFinished = { results += 1; phase = JunkCleanPhase.Complete },
                    onExit = { effect ->
                        exiting = true
                        flow.exit(FeatureKey.JUNK_CLEAN, effect.reason) {}
                    },
                    onDeleteAuthorization = { authorizations += it },
                )
            }
    }
}
