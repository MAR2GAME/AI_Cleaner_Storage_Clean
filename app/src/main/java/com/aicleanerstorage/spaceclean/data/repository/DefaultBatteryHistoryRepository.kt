package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.battery.BatteryHistoryFileStore

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/** 鎶婄數姹犲巻鍙叉牱鏈寔涔呭寲鍒板簲鐢ㄧ鏈夋枃浠讹紝骞舵寜绐楀彛璇诲彇銆?*/
internal class DefaultBatteryHistoryRepository(
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

    override fun observeSamples(): Flow<List<BatteryHistorySampleModel>> = _samples.asStateFlow()

    override fun latestSample(): BatteryHistorySampleModel? = _samples.value.lastOrNull()

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
            if (sample.timestampMillis >= nowMillis - BatteryHistoryRepository.WINDOW_MILLIS) {
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
        sampleCount > BatteryHistoryRepository.MAX_SAMPLES ||
            nowMillis - lastCompactAtMillis >= BATTERY_HISTORY_COMPACT_INTERVAL_MILLIS

    private fun normalize(
        samples: List<BatteryHistorySampleModel>,
        nowMillis: Long,
    ): List<BatteryHistorySampleModel> {
        val cutoff = nowMillis - BatteryHistoryRepository.WINDOW_MILLIS
        val latestAllowed = nowMillis + BatteryHistoryRepository.FUTURE_TOLERANCE_MILLIS
        val byTimestamp = LinkedHashMap<Long, BatteryHistorySampleModel>()
        samples
            .asSequence()
            .filter { it.isUsable() }
            .filter { it.timestampMillis in cutoff..latestAllowed }
            .sortedBy { it.timestampMillis }
            .forEach { byTimestamp[it.timestampMillis] = it }
        return byTimestamp.values
            .sortedBy { it.timestampMillis }
            .takeLast(BatteryHistoryRepository.MAX_SAMPLES)
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

