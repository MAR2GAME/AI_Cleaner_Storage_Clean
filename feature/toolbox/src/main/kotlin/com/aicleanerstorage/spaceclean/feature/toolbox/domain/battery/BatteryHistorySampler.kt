package com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySample

enum class BatteryHistoryOwner {
    Service,
    BatteryPage,
}

interface BatteryHistorySampler {
    fun start(owner: BatteryHistoryOwner)

    fun stop(owner: BatteryHistoryOwner)

    fun sampleOnce(force: Boolean = false): BatteryHistorySample?
}

