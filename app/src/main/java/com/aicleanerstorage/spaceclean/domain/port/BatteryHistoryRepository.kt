package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 电池历史样本的读取与追加契约。
 *
 * 只保留最近 [WINDOW_MILLIS] 内的样本；查询结果按时间升序，
 * 存储层负责丢弃过旧或时间明显超前的样本。
 */
interface BatteryHistoryRepository {
    /** 当前窗口内的采样列表，供页面订阅。 */
    val samples: StateFlow<List<BatteryHistorySampleModel>>

    /**
     * 读取最近窗口内的样本。
     *
     * @param nowMillis 当前时间；测试可注入固定值。
     * @return 按时间升序的样本列表；没有数据时为空列表。
     */
    fun loadRecent(nowMillis: Long = System.currentTimeMillis()): List<BatteryHistorySampleModel>

    /**
     * 追加一条采样并返回更新后的窗口。
     *
     * @param sample 待追加样本。
     * @param nowMillis 当前时间；用于过滤过旧和未来样本。
     * @return 更新后的按时间升序样本列表。
     */
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
