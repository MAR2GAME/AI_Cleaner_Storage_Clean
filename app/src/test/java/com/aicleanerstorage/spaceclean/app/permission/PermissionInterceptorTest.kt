package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.common.permission.*
import com.aicleanerstorage.spaceclean.core.presentation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionInterceptorTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `granted short circuits and unrelated policy never checks permissions`() = runTest {
        var checks = 0
        val subject = PermissionInterceptor({ checks++; true })
        assertEquals(InterceptResult.NotHandled, subject.intercept(object : IntentPolicy { override val priority = 1 }))
        assertEquals(0, checks)
        assertEquals(InterceptResult.Continue, subject.intercept(PermissionNeed.StorageFiles))
        assertNull(subject.request.value)
    }

    @Test fun `busy does not replace owner and dismissal blocks only that request`() = runTest {
        val subject = PermissionInterceptor({ false })
        val first = async { subject.intercept(PermissionNeed.StorageFiles) }
        runCurrent()
        val owner = subject.request.value!!.owner
        assertEquals(InterceptResult.Busy, subject.intercept(PermissionNeed.StorageFiles))
        assertSame(owner, subject.request.value!!.owner)
        subject.dismiss(owner)
        assertNull(subject.request.value)
        assertEquals(InterceptResult.Block, first.await())
    }

    @Test fun `settings return rechecks real permission once and ignores stale callbacks`() = runTest {
        var granted = false
        val subject = PermissionInterceptor({ granted })
        val first = async { subject.ensure(PermissionNeed.StorageFiles) }
        runCurrent()
        val old = subject.request.value!!.owner
        subject.confirm(old)
        assertTrue(subject.beginLaunch(old, false))
        subject.settingsReturned(old)
        assertFalse(first.isCompleted)
        granted = true
        subject.markPaused(old)
        subject.settingsReturned(old)
        subject.settingsReturned(old, true)
        assertEquals(PermissionResult.Granted, first.await())
        granted = false
        val second = async { subject.ensure(PermissionNeed.StorageFiles) }
        runCurrent()
        val current = subject.request.value!!.owner
        subject.dismiss(old)
        subject.settingsReturned(old, true)
        assertSame(current, subject.request.value!!.owner)
        subject.dismiss(current)
        assertEquals(PermissionResult.Denied, second.await())
    }

    @Test fun `multiple permissions retain order and only finish after last real grant`() = runTest {
        val granted = mutableSetOf<PermissionType>()
        val subject = PermissionInterceptor({ it in granted })
        val result = async { subject.ensure(PermissionTarget.Action(ProtectedAction.AppLockEnableMonitoring)) }
        runCurrent()
        val owner = subject.request.value!!.owner
        assertEquals(PermissionType.UsageAccess, subject.request.value!!.missing)
        subject.confirm(owner)
        subject.beginLaunch(owner, false)
        granted += PermissionType.UsageAccess
        subject.settingsReturned(owner, true)
        assertEquals(PermissionType.Overlay, subject.request.value!!.missing)
        assertEquals(PermissionInterceptor.Stage.AwaitingUser, subject.request.value!!.stage)
        subject.settingsReturned(owner, true)
        assertFalse(result.isCompleted)
        subject.confirm(owner)
        subject.beginLaunch(owner, false)
        granted += PermissionType.Overlay
        subject.settingsReturned(owner, true)
        assertEquals(PermissionResult.Granted, result.await())
    }

    @Test fun `cancellation before launch releases and after launch keeps ownership until return`() = runTest {
        for (started in listOf(false, true)) {
            val subject = PermissionInterceptor({ false })
            val result = async { subject.ensure(PermissionNeed.StorageFiles) }
            runCurrent()
            val owner = subject.request.value!!.owner
            if (started) { subject.confirm(owner); subject.beginLaunch(owner, false) }
            result.cancelAndJoin()
            if (started) {
                assertEquals(PermissionResult.Busy, subject.ensure(PermissionNeed.StorageFiles))
                subject.settingsReturned(owner, true)
            }
            assertNull(subject.request.value)
            assertTrue(result.isCancelled)
        }
    }

    @Test fun `host destruction cancels waiting business and releases pending system request`() = runTest {
        val subject = PermissionInterceptor({ false })
        val result = async { subject.ensure(PermissionNeed.StorageFiles) }
        runCurrent()
        val owner = subject.request.value!!.owner
        subject.confirm(owner)
        subject.beginLaunch(owner, true)
        subject.cancelAll()
        runCurrent()
        assertTrue(result.isCancelled)
        assertNull(subject.request.value)
        assertFalse(subject.runtimeReturned(owner))
    }

    @Test fun `only launched runtime records request and actual non abandoned refusal`() = runTest {
        val requested = mutableListOf<PermissionType>()
        val denied = mutableListOf<PermissionType>()
        val subject = PermissionInterceptor({ false }, requested::add, denied::add)
        val failed = async { subject.ensure(PermissionNeed.StorageFiles) }
        runCurrent()
        subject.launchFailed(subject.request.value!!.owner)
        assertEquals(PermissionResult.Denied, failed.await())
        assertTrue(requested.isEmpty())
        assertTrue(denied.isEmpty())
        for (cancel in listOf(false, true)) {
            val result = async { subject.ensure(PermissionNeed.StorageFiles) }
            runCurrent()
            val owner = subject.request.value!!.owner
            subject.confirm(owner)
            assertTrue(subject.beginLaunch(owner, true))
            assertFalse(subject.beginLaunch(owner, true))
            subject.runtimeStarted(owner)
            if (cancel) result.cancelAndJoin()
            assertTrue(subject.runtimeReturned(owner))
            assertFalse(subject.runtimeReturned(owner))
            if (!cancel) assertEquals(PermissionResult.Denied, result.await())
        }
        assertEquals(2, requested.size)
        assertEquals(listOf(PermissionType.StorageFiles), denied)
    }

    @Test fun `direct settings path explains but explicit settings does not`() = runTest {
        val subject = PermissionInterceptor({ false })
        val direct = async { subject.ensure(PermissionTarget.Permission(PermissionType.UsageAccess), PermissionPromptMode.Direct) }
        runCurrent()
        val owner = subject.request.value!!.owner
        assertTrue(subject.explainSettings(owner))
        subject.confirm(owner)
        assertFalse(subject.explainSettings(owner))
        direct.cancelAndJoin()
        val settings = async { subject.openSettings(PermissionType.UsageAccess) }
        runCurrent()
        assertFalse(subject.explainSettings(subject.request.value!!.owner))
        settings.cancelAndJoin()
    }
}
