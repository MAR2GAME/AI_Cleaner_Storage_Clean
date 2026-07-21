package com.aicleanerstorage.spaceclean.feature.toolbox.battery.data

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.model.BatteryHistorySample
import kotlinx.coroutines.flow.StateFlow

interface BatteryHistoryRepository {
    val samples: StateFlow<List<BatteryHistorySample>>

    fun loadRecent(nowMillis: Long = System.currentTimeMillis()): List<BatteryHistorySample>

    fun append(
        sample: BatteryHistorySample,
        nowMillis: Long = System.currentTimeMillis(),
    ): List<BatteryHistorySample>
}
