package com.aicleanerstorage.spaceclean.feature.toolbox.data.battery

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingPreferences
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySampler


interface BatterySamplingServiceActions {
    fun syncSamplingState()

    fun enableSampling()

    fun disableSampling()
}

class BatterySamplingCoordinator(
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

