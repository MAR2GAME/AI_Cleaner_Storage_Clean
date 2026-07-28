package com.aicleanerstorage.spaceclean.feature.toolbox.battery.data

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySampler

internal interface BatterySamplingServiceActions {
    fun syncSamplingState()

    fun enableSampling()

    fun disableSampling()
}

internal class BatterySamplingCoordinator(
    private val sampler: BatteryHistorySampler,
    private val preferences: BackgroundBatterySamplingPreferences,
    private val hasNotificationPermission: () -> Boolean,
) : BatterySamplingServiceActions {
    override fun syncSamplingState() {
        if (preferences.isBackgroundSamplingEnabled() && hasNotificationPermission()) {
            enableSampling()
        } else {
            disableSampling()
        }
    }

    override fun enableSampling() {
        runCatching { sampler.start(BatteryHistoryOwner.Service) }
    }

    override fun disableSampling() {
        runCatching { sampler.stop(BatteryHistoryOwner.Service) }
    }
}
