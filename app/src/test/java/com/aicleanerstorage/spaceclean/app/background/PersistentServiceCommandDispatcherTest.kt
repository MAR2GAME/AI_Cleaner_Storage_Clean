package com.aicleanerstorage.spaceclean.app.background

import kotlinx.coroutines.test.advanceTimeBy

import kotlinx.coroutines.test.runCurrent

import kotlinx.coroutines.test.runTest

import kotlinx.coroutines.test.TestCoroutineScheduler

import kotlinx.coroutines.test.StandardTestDispatcher

import kotlinx.coroutines.ExperimentalCoroutinesApi

import java.lang.reflect.Proxy

import com.aicleanerstorage.devicekit.DeviceInfoRepository

import com.aicleanerstorage.devicekit.model.BatteryInfo

import com.aicleanerstorage.spaceclean.data.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistorySampleModel
import kotlinx.coroutines.flow.MutableStateFlow

import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository

import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockServiceActions
import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PersistentServiceCommandDispatcherTest {
    @Test
    fun `all external actions keep their command mapping`() {
        val mappings =
            mapOf(
                PersistentServiceActions.START to PersistentServiceCommand.Start,
                PersistentServiceActions.ENABLE_MONITORING to PersistentServiceCommand.EnableMonitoring,
                PersistentServiceActions.DISABLE_MONITORING to PersistentServiceCommand.DisableMonitoring,
                PersistentServiceActions.ENABLE_BATTERY_SAMPLING to PersistentServiceCommand.EnableBatterySampling,
                PersistentServiceActions.DISABLE_BATTERY_SAMPLING to PersistentServiceCommand.DisableBatterySampling,
                PersistentServiceActions.RESTORE_NOTIFICATION to PersistentServiceCommand.RestoreNotification,
                PersistentServiceActions.STOP_SERVICE to PersistentServiceCommand.StopService,
                PersistentServiceActions.PASSWORD_SUCCESS to PersistentServiceCommand.DismissLockScreen,
                PersistentServiceActions.LOCK_SCREEN_CANCELLED to PersistentServiceCommand.DismissLockScreen,
            )

        mappings.forEach { (action, expected) ->
            assertEquals(expected, persistentServiceCommand(action))
        }
        assertNull(persistentServiceCommand(null))
        assertNull(persistentServiceCommand("unknown"))
    }

    @Test
    fun `dispatcher delegates every command once`() {
        val appLock = RecordingAppLockActions()
        val notification = RecordingNotificationActions()
        val batterySampling = RecordingBatterySamplingActions()
        var stopCalls = 0
        val dispatcher =
            PersistentServiceCommandDispatcher(
                appLock = appLock,
                batterySampling = batterySampling,
                notification = notification,
                stopService = { stopCalls += 1 },
            )

        PersistentServiceCommand.entries.forEach(dispatcher::dispatch)

        assertEquals(
            listOf("sync", "enable", "disable", "dismiss"),
            appLock.calls,
        )
        assertEquals(1, notification.restoreCalls)
        assertEquals(listOf("sync", "enable", "disable"), batterySampling.calls)
        assertEquals(1, stopCalls)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `service and page share sampling until the last owner stops`() = runTest {
        val history = TestBatteryHistory()
        val sampler = sampler(history, testScheduler)
        val settings = InMemorySettingsRepository(backgroundSampling = true)
        val coordinator = BatterySamplingCoordinator(sampler, settings) { true }

        coordinator.syncSamplingState()
        runCurrent()
        assertEquals(1, history.samples.value.size)
        sampler.start(BatteryHistoryOwner.BatteryPage)
        coordinator.disableSampling()
        advanceTimeBy(100)
        runCurrent()
        assertEquals(2, history.samples.value.size)
        sampler.stop(BatteryHistoryOwner.BatteryPage)
        advanceTimeBy(100)
        runCurrent()
        assertEquals(2, history.samples.value.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `battery coordinator does not sample without preference or notification permission`() = runTest {
        val history = TestBatteryHistory()
        val sampler = sampler(history, testScheduler)
        BatterySamplingCoordinator(sampler, InMemorySettingsRepository()) { true }.syncSamplingState()
        runCurrent()
        assertEquals(0, history.samples.value.size)
        BatterySamplingCoordinator(sampler, InMemorySettingsRepository(backgroundSampling = true)) { false }.syncSamplingState()
        runCurrent()
        assertEquals(0, history.samples.value.size)
    }

    private class TestBatteryHistory : BatteryHistoryRepository {
        override val samples = MutableStateFlow<List<BatteryHistorySampleModel>>(emptyList())

        override fun loadRecent(nowMillis: Long) = samples.value

        override fun append(sample: BatteryHistorySampleModel, nowMillis: Long): List<BatteryHistorySampleModel> {
            samples.value = samples.value + sample
            return samples.value
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun sampler(history: BatteryHistoryRepository, scheduler: TestCoroutineScheduler): BatteryHistorySampler {
        val device = Proxy.newProxyInstance(
            DeviceInfoRepository::class.java.classLoader, arrayOf(DeviceInfoRepository::class.java),
        ) { _, method, _ ->
            when (method.name) {
                "batteryInfo" -> BatteryInfo(80, "Good", 29f, 4000, "Li-ion", 4000)
                "batteryCurrentNowMa" -> 200f
                else -> error("Unexpected device call: ${method.name}")
            }
        } as DeviceInfoRepository
        return BatteryHistorySampler(device, history, StandardTestDispatcher(scheduler), { 1000L + scheduler.currentTime }, 100L)
    }
}

private class RecordingAppLockActions : AppLockServiceActions {
    val calls = mutableListOf<String>()

    override fun syncMonitoringState() {
        calls += "sync"
    }

    override fun enableMonitoring() {
        calls += "enable"
    }

    override fun disableMonitoring() {
        calls += "disable"
    }

    override fun dismissLockScreen() {
        calls += "dismiss"
    }
}

private class RecordingNotificationActions : PersistentNotificationActions {
    var restoreCalls = 0

    override fun scheduleRestore() {
        restoreCalls += 1
    }
}

private class RecordingBatterySamplingActions : BatterySamplingServiceActions {
    val calls = mutableListOf<String>()

    override fun syncSamplingState() {
        calls += "sync"
    }

    override fun enableSampling() {
        calls += "enable"
    }

    override fun disableSampling() {
        calls += "disable"
    }
}
