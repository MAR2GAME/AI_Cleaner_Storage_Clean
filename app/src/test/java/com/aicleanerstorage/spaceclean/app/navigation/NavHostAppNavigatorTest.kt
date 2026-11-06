package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavHostAppNavigatorTest {
    @Test fun `duplicate entry waits for one ad and commits once`() = runTest {
        val requests = EntryNavigationRequests(this)
        val closed = CompletableDeferred<Unit>()
        var ads = 0
        var navigations = 0
        repeat(2) {
            requests.start("home", AppDestination.JunkClean) {
                ads++
                closed.await()
                ensureActive()
                navigations++
            }
        }
        runCurrent()
        assertEquals(1, ads)
        assertEquals(0, navigations)
        closed.complete(Unit)
        runCurrent()
        assertEquals(1, navigations)
    }

    @Test fun `replaced request ignores noncancellable late ad closure`() = runTest {
        val requests = EntryNavigationRequests(this)
        val closed = CompletableDeferred<Unit>()
        val visited = mutableListOf<AppDestination>()
        requests.start("home", AppDestination.JunkClean) {
            withContext(NonCancellable) { closed.await() }
            ensureActive()
            visited += AppDestination.JunkClean
        }
        runCurrent()
        requests.start("home", AppDestination.Settings) { visited += AppDestination.Settings }
        runCurrent()
        closed.complete(Unit)
        runCurrent()
        assertEquals(listOf(AppDestination.Settings), visited)
    }

    @Test fun `explicit back and page disposal cancel pending navigation`() = runTest {
        for (dispose in listOf(false, true)) {
            val scope = CoroutineScope(coroutineContext + Job())
            val requests = EntryNavigationRequests(scope)
            val closed = CompletableDeferred<Unit>()
            var navigated = false
            requests.start("home", AppDestination.NetworkScan) {
                closed.await()
                ensureActive()
                navigated = true
            }
            runCurrent()
            if (dispose) scope.cancel() else requests.cancel()
            closed.complete(Unit)
            runCurrent()
            assertFalse(navigated)
            scope.cancel()
        }
    }

    @Test fun `completed or cancelled request releases its key`() = runTest {
        val requests = EntryNavigationRequests(this)
        var navigations = 0
        repeat(2) {
            requests.start("home", AppDestination.JunkClean) { navigations++ }
            runCurrent()
        }
        assertEquals(2, navigations)
    }

    @Test fun `cancelled request can be retried for the same source and target`() = runTest {
        val requests = EntryNavigationRequests(this)
        var navigations = 0
        val gate = CompletableDeferred<Unit>()

        requests.start("home", AppDestination.ManagePermissions) {
            gate.await()
            ensureActive()
            navigations++
        }
        runCurrent()
        requests.cancel()
        requests.start("home", AppDestination.ManagePermissions) { navigations++ }
        runCurrent()

        assertEquals(1, navigations)
    }
}
