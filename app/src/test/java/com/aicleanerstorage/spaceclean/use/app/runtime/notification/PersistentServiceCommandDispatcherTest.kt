package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.model.BatteryHistorySample
import com.aicleanerstorage.spaceclean.feature.applock.service.AppLockServiceActions
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatterySamplingCoordinator
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatterySamplingServiceActions
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BackgroundBatterySamplingPreferences
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySampler
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

    @Test
    fun `battery coordinator uses only the service owner`() {
        val sampler = RecordingBatterySampler()
        val preferences = RecordingBackgroundBatterySamplingPreferences(enabled = true)
        val coordinator = BatterySamplingCoordinator(sampler, preferences) { true }

        coordinator.syncSamplingState()
        coordinator.disableSampling()

        assertEquals(listOf(BatteryHistoryOwner.Service), sampler.started)
        assertEquals(listOf(BatteryHistoryOwner.Service), sampler.stopped)
    }

    @Test
    fun `battery coordinator does not sample without preference or notification permission`() {
        val sampler = RecordingBatterySampler()
        val disabledPreferences = RecordingBackgroundBatterySamplingPreferences(enabled = false)
        BatterySamplingCoordinator(sampler, disabledPreferences) { true }.syncSamplingState()

        val enabledPreferences = RecordingBackgroundBatterySamplingPreferences(enabled = true)
        BatterySamplingCoordinator(sampler, enabledPreferences) { false }.syncSamplingState()

        assertEquals(emptyList<BatteryHistoryOwner>(), sampler.started)
        assertEquals(
            listOf(BatteryHistoryOwner.Service, BatteryHistoryOwner.Service),
            sampler.stopped,
        )
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

private class RecordingBackgroundBatterySamplingPreferences(
    private var enabled: Boolean,
) : BackgroundBatterySamplingPreferences {
    override fun isBackgroundSamplingEnabled(): Boolean = enabled

    override fun enableBackgroundSampling() {
        enabled = true
    }
}

private class RecordingBatterySampler : BatteryHistorySampler {
    val started = mutableListOf<BatteryHistoryOwner>()
    val stopped = mutableListOf<BatteryHistoryOwner>()

    override fun start(owner: BatteryHistoryOwner) {
        started += owner
    }

    override fun stop(owner: BatteryHistoryOwner) {
        stopped += owner
    }

    override fun sampleOnce(force: Boolean): BatteryHistorySample? = null
}
