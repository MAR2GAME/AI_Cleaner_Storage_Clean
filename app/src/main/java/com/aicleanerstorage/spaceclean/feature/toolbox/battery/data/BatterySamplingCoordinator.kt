package com.aicleanerstorage.spaceclean.feature.toolbox.battery.data

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySampler

internal class BatterySamplingCoordinator(
    private val sampler: BatteryHistorySampler,
) {
    fun start() {
        runCatching { sampler.start(BatteryHistoryOwner.Service) }
    }

    fun stop() {
        runCatching { sampler.stop(BatteryHistoryOwner.Service) }
    }
}
