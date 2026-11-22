package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import kotlinx.coroutines.flow.Flow

/**
 * 鐢垫睜鍘嗗彶鏍锋湰鐨勮鍙栦笌杩藉姞濂戠害銆? *
 * 鍙繚鐣欐渶杩?[WINDOW_MILLIS] 鍐呯殑鏍锋湰锛涙煡璇㈢粨鏋滄寜鏃堕棿鍗囧簭锛? * 瀛樺偍灞傝礋璐ｄ涪寮冭繃鏃ф垨鏃堕棿鏄庢樉瓒呭墠鐨勬牱鏈€? */
interface BatteryHistoryRepository {
    /**
     * 观察当前窗口内的采样列表。
     *
     * @return 采样列表变化流；订阅时立即收到当前值。
     */
    fun observeSamples(): Flow<List<BatteryHistorySampleModel>>

    /**
     * 读取当前最新一条采样。
     *
     * @return 最新采样；没有数据时返回 null。
     */
    fun latestSample(): BatteryHistorySampleModel?

    /**
     * 璇诲彇鏈€杩戠獥鍙ｅ唴鐨勬牱鏈€?     *
     * @param nowMillis 褰撳墠鏃堕棿锛涙祴璇曞彲娉ㄥ叆鍥哄畾鍊笺€?     * @return 鎸夋椂闂村崌搴忕殑鏍锋湰鍒楄〃锛涙病鏈夋暟鎹椂涓虹┖鍒楄〃銆?     */
    fun loadRecent(nowMillis: Long = System.currentTimeMillis()): List<BatteryHistorySampleModel>

    /**
     * 杩藉姞涓€鏉￠噰鏍峰苟杩斿洖鏇存柊鍚庣殑绐楀彛銆?     *
     * @param sample 寰呰拷鍔犳牱鏈€?     * @param nowMillis 褰撳墠鏃堕棿锛涚敤浜庤繃婊よ繃鏃у拰鏈潵鏍锋湰銆?     * @return 鏇存柊鍚庣殑鎸夋椂闂村崌搴忔牱鏈垪琛ㄣ€?     */
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

