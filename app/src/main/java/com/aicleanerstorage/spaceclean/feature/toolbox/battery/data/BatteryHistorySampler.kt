package com.aicleanerstorage.spaceclean.feature.toolbox.battery.data

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.model.BatteryHistorySample

enum class BatteryHistoryOwner {
    Service,
    BatteryPage,
}

interface BatteryHistorySampler {
    fun start(owner: BatteryHistoryOwner)

    fun stop(owner: BatteryHistoryOwner)

    fun sampleOnce(force: Boolean = false): BatteryHistorySample?
}
