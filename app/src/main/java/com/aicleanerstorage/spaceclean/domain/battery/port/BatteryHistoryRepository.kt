package com.aicleanerstorage.spaceclean.domain.battery.port

import com.aicleanerstorage.spaceclean.domain.battery.model.BatteryHistorySampleModel
import kotlinx.coroutines.flow.StateFlow

interface BatteryHistoryRepository {
    val samples: StateFlow<List<BatteryHistorySampleModel>>

    fun loadRecent(nowMillis: Long = System.currentTimeMillis()): List<BatteryHistorySampleModel>

    fun append(
        sample: BatteryHistorySampleModel,
        nowMillis: Long = System.currentTimeMillis(),
    ): List<BatteryHistorySampleModel>

    companion object {
        const val SAMPLE_INTERVAL_MILLIS = 2_000L
        const val WINDOW_MILLIS = 24L * 60L * 60L * 1000L
        const val MAX_SAMPLES = (WINDOW_MILLIS / SAMPLE_INTERVAL_MILLIS).toInt() + 1
        const val FUTURE_TOLERANCE_MILLIS = 60_000L
    }
}
