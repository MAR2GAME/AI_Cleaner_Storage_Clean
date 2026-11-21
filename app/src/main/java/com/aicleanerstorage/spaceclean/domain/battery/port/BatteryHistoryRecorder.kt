package com.aicleanerstorage.spaceclean.domain.battery.port

import com.aicleanerstorage.spaceclean.domain.battery.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.battery.model.BatteryHistorySampleModel

/** Starts and stops background battery sampling for a page or service owner. */
interface BatteryHistoryRecorder {
    fun start(owner: BatteryHistoryOwner)

    fun stop(owner: BatteryHistoryOwner)

    fun sampleOnce(force: Boolean = false): BatteryHistorySampleModel?
}
