package com.aicleanerstorage.spaceclean.app.permission

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationPermissionEffectsTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `entering an already resumed home evaluates once after 350 milliseconds`() = runTest {
        val owner = Owner()
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        var evaluations = 0
        val job = launch { owner.lifecycle.observeNotificationHomePrompt { evaluations++ } }
        runCurrent()
        advanceTimeBy(349)
        runCurrent()
        assertEquals(0, evaluations)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, evaluations)
        owner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(1, evaluations)
        job.cancelAndJoin()
    }

    @Test fun `pause cancels pending evaluation and resume starts one fresh delay`() = runTest {
        val owner = Owner()
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        var evaluations = 0
        val job = launch { owner.lifecycle.observeNotificationHomePrompt { evaluations++ } }
        runCurrent()
        advanceTimeBy(200)
        owner.lifecycle.currentState = Lifecycle.State.STARTED
        runCurrent()
        advanceTimeBy(1000)
        assertEquals(0, evaluations)
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        advanceTimeBy(349)
        runCurrent()
        assertEquals(0, evaluations)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, evaluations)
        job.cancelAndJoin()
    }

    @Test fun `leaving home cancels delay even if activity remains resumed`() = runTest {
        val owner = Owner()
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        var evaluations = 0
        val job = launch { owner.lifecycle.observeNotificationHomePrompt { evaluations++ } }
        runCurrent()
        advanceTimeBy(349)
        job.cancelAndJoin()
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(0, evaluations)
    }

    @Test fun `destroying host releases observation without showing prompt`() = runTest {
        val owner = Owner()
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        var evaluations = 0
        val job = launch { owner.lifecycle.observeNotificationHomePrompt { evaluations++ } }
        runCurrent()
        owner.lifecycle.currentState = Lifecycle.State.DESTROYED
        runCurrent()
        assertTrue(job.isCompleted)
        assertEquals(0, evaluations)
    }

    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this)
    }
}
