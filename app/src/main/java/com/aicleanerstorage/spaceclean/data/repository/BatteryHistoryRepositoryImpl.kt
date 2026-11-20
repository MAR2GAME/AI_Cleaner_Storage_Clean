package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.domain.rule.BatteryHistoryRule

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.BatteryHistoryFileStore
import com.aicleanerstorage.spaceclean.domain.model.BatteryHistorySampleModel
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BatteryHistoryRepositoryImpl(
    historyFile: File,
) : BatteryHistoryRepository {
    constructor(context: Context) : this(
        File(context.applicationContext.filesDir, HISTORY_FILE_NAME),
    )

    private val store = BatteryHistoryFileStore(historyFile)
    private val lock = Any()
    private val _samples = MutableStateFlow<List<BatteryHistorySampleModel>>(emptyList())
    private var loaded = false
    private var lastCompactAtMillis = 0L

    override val samples: StateFlow<List<BatteryHistorySampleModel>> = _samples.asStateFlow()

    override fun loadRecent(nowMillis: Long): List<BatteryHistorySampleModel> =
        synchronized(lock) {
            loadRecentLocked(nowMillis, compact = true)
        }

    override fun append(
        sample: BatteryHistorySampleModel,
        nowMillis: Long,
    ): List<BatteryHistorySampleModel> =
        synchronized(lock) {
            val current = if (loaded) _samples.value else loadRecentLocked(nowMillis, compact = false)
            if (!sample.isUsable()) {
                return@synchronized current
            }

            val next =
                normalize(
                    samples = current.filterNot { it.timestampMillis == sample.timestampMillis } + sample,
                    nowMillis = nowMillis,
                )
            if (sample.timestampMillis >= nowMillis - BatteryHistoryRule.WINDOW_MILLIS) {
                store.appendRecord(sample)
            }
            _samples.value = next

            if (shouldCompact(nowMillis, next.size)) {
                store.rewrite(next)
                lastCompactAtMillis = nowMillis
            }
            next
        }

    private fun loadRecentLocked(
        nowMillis: Long,
        compact: Boolean,
    ): List<BatteryHistorySampleModel> {
        val raw = store.readAllRecords()
        val recent = normalize(raw, nowMillis)
        loaded = true
        _samples.value = recent
        if (compact && (raw.size != recent.size || shouldCompact(nowMillis, recent.size))) {
            store.rewrite(recent)
            lastCompactAtMillis = nowMillis
        }
        return recent
    }

    private fun shouldCompact(
        nowMillis: Long,
        sampleCount: Int,
    ): Boolean =
        sampleCount > BatteryHistoryRule.MAX_SAMPLES ||
            nowMillis - lastCompactAtMillis >= BATTERY_HISTORY_COMPACT_INTERVAL_MILLIS

    private fun normalize(
        samples: List<BatteryHistorySampleModel>,
        nowMillis: Long,
    ): List<BatteryHistorySampleModel> {
        val cutoff = nowMillis - BatteryHistoryRule.WINDOW_MILLIS
        val latestAllowed = nowMillis + BatteryHistoryRule.FUTURE_TOLERANCE_MILLIS
        val byTimestamp = LinkedHashMap<Long, BatteryHistorySampleModel>()
        samples
            .asSequence()
            .filter { it.isUsable() }
            .filter { it.timestampMillis in cutoff..latestAllowed }
            .sortedBy { it.timestampMillis }
            .forEach { byTimestamp[it.timestampMillis] = it }
        return byTimestamp.values
            .sortedBy { it.timestampMillis }
            .takeLast(BatteryHistoryRule.MAX_SAMPLES)
    }

    companion object {
        private const val HISTORY_FILE_NAME = "battery_history.bin"
        private const val BATTERY_HISTORY_COMPACT_INTERVAL_MILLIS = 10L * 60L * 1000L
    }
}

private fun BatteryHistorySampleModel.isUsable(): Boolean =
    timestampMillis > 0L &&
        temperatureC.isFiniteValue() &&
        currentMa?.isFiniteValue() != false

private fun Float.isFiniteValue(): Boolean = !isNaN() && !isInfinite()

