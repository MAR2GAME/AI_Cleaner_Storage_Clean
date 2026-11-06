package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationCoordinatorTest {
    @Test
    fun duplicateRequestReusesTicketAndRunsOnce() = runTest {
        val coordinator = NavigationCoordinator(this)
        var executions = 0

        val first = coordinator.submit("home", AppDestination.JunkClean, NavigationKind.Navigate) {
            executions++
            true
        }
        val second = coordinator.submit("home", AppDestination.JunkClean, NavigationKind.Navigate) {
            executions++
            true
        }

        runCurrent()

        assertEquals(first.requestId, second.requestId)
        assertEquals(1, executions)
        assertTrue(first.completion.await().accepted)
    }

    @Test
    fun newRequestCancelsOldRequest() = runTest {
        val coordinator = NavigationCoordinator(this)
        val gate = CompletableDeferred<Unit>()
        var oldExecuted = false
        var newExecuted = false

        coordinator.submit("home", AppDestination.JunkClean, NavigationKind.Navigate) {
            gate.await()
            oldExecuted = true
            true
        }
        runCurrent()
        coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) {
            newExecuted = true
            true
        }
        runCurrent()
        gate.complete(Unit)
        runCurrent()

        assertFalse(oldExecuted)
        assertTrue(newExecuted)
    }

    @Test
    fun cancelledActionPropagatesCancellation() = runTest {
        val coordinator = NavigationCoordinator(this)
        val ticket = coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) {
            throw CancellationException("cancelled")
        }

        runCurrent()

        assertTrue(ticket.completion.isCancelled)
    }

    @Test
    fun failedActionReturnsRejectedReceipt() = runTest {
        val coordinator = NavigationCoordinator(this)
        val ticket = coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) {
            error("navigation failed")
        }

        runCurrent()

        assertFalse(ticket.completion.await().accepted)
    }

    @Test
    fun sourceChangeCancelsBeforeActionStarts() = runTest {
        val coordinator = NavigationCoordinator(this)
        var called = false
        val ticket = coordinator.submit("old", AppDestination.Settings, NavigationKind.Navigate) {
            called = true
            true
        }
        coordinator.sourceChanged("new")
        runCurrent()
        assertFalse(called)
        assertTrue(ticket.completion.isCancelled)
    }

    @Test
    fun oldSourceCannotCancelNewSource() = runTest {
        val coordinator = NavigationCoordinator(this)
        val ticket = coordinator.submit("new", AppDestination.Settings, NavigationKind.Navigate) { true }
        coordinator.cancelForSource("old")
        runCurrent()
        assertTrue(ticket.completion.await().accepted)
    }

    @Test
    fun nonCancellableLateActionCannotPublishAcceptance() = runTest {
        val coordinator = NavigationCoordinator(this)
        val events = mutableListOf<NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { coordinator.events.collect { events += it } }
        val barrier = CompletableDeferred<Unit>()
        val old = coordinator.submit("home", AppDestination.JunkClean, NavigationKind.Navigate) {
            withContext(NonCancellable) { barrier.await() }
            true
        }
        runCurrent()
        coordinator.submit("home", AppDestination.Settings, NavigationKind.ResetTo) { true }
        runCurrent()
        barrier.complete(Unit)
        runCurrent()
        assertTrue(old.completion.isCancelled)
        assertEquals(1, events.filterIsInstance<NavigationEvent.Cancelled>().size)
        assertEquals(listOf(AppDestination.Settings), events.filterIsInstance<NavigationEvent.Accepted>().map { it.request.destination })
    }

    @Test
    fun parentCancellationSettlesTicketEvenBeforeStart() = runTest {
        val parent = Job()
        val coordinator = NavigationCoordinator(CoroutineScope(coroutineContext + parent))
        val ticket = coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) { true }
        parent.cancel()
        runCurrent()
        assertTrue(ticket.completion.isCancelled)
    }

    @Test
    fun completedRequestCanBeSubmittedAgainAndKindsHaveDistinctKeys() = runTest {
        val coordinator = NavigationCoordinator(this)
        val first = coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) { true }
        val replacement = coordinator.submit("home", AppDestination.Settings, NavigationKind.ReplaceCurrent) { true }
        runCurrent()
        assertTrue(first.completion.isCancelled)
        assertTrue(replacement.completion.await().accepted)
        val again = coordinator.submit("home", AppDestination.Settings, NavigationKind.ReplaceCurrent) { true }
        runCurrent()
        assertTrue(again.requestId > replacement.requestId)
        assertTrue(again.completion.await().accepted)
    }

    @Test
    fun topLevelCancelsWaitingNavigation() = runTest {
        val coordinator = NavigationCoordinator(this)
        val first = coordinator.submit("home", AppDestination.Settings, NavigationKind.Navigate) {
            awaitCancellation()
        }
        runCurrent()
        val top = coordinator.submit("home", AppDestination.Main(), NavigationKind.TopLevel) { true }
        runCurrent()
        assertTrue(first.completion.isCancelled)
        assertTrue(top.completion.await().accepted)
    }
}
