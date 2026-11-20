package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.BatteryHistorySampleModel
import kotlinx.coroutines.flow.StateFlow

interface BatteryHistoryRepository {
    val samples: StateFlow<List<BatteryHistorySampleModel>>

    fun loadRecent(nowMillis: Long = System.currentTimeMillis()): List<BatteryHistorySampleModel>

    fun append(
        sample: BatteryHistorySampleModel,
        nowMillis: Long = System.currentTimeMillis(),
    ): List<BatteryHistorySampleModel>
}

