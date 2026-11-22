package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageInfoModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 网络状态、局域网扫描、测速与流量统计的业务契约。
 *
 * [lastScan] 由扫描页和局域网设备页共享；[runSpeedTest] 的 [onProgress]
 * 在 IO 线程回调，UI 只把它转成 Action，不直接改状态。
 */
interface NetworkRepository {
    fun readNetworkInfo(): NetworkInfoModel

    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfoModel

    suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel

    val lastScan: StateFlow<NetworkScanResultModel?>

    suspend fun scanWifi(): NetworkScanResultModel

    fun clearLastScan()
}
