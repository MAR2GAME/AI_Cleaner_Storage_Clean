package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageInfoModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 网络状态、局域网扫描、测速与流量统计的业务契约。
 *
 * [lastScan] 由扫描页和局域网设备页共享；`runSpeedTest` 的 [onProgress]
 * 在 IO 线程回调，UI 只把它转成 Action，不直接改状态。
 */
interface NetworkRepository {
    /**
     * 读取当前网络连接信息。
     *
     * @return 网络类型、名称与连接状态。
     */
    fun readNetworkInfo(): NetworkInfoModel

    /**
     * 判断当前是否有可用网络。
     *
     * @return true 表示存在已连接网络。
     */
    fun isNetworkAvailable(): Boolean

    /**
     * 判断当前是否连接 Wi-Fi。
     *
     * @return true 表示 Wi-Fi 已连接。
     */
    fun isWifiConnected(): Boolean

    /**
     * 判断是否拥有使用情况访问权限。
     *
     * @return true 表示可以读取流量统计。
     */
    fun hasNetworkUsageAccess(): Boolean

    /**
     * 读取流量统计。
     *
     * @return 流量汇总与应用明细；无权限时由实现返回降级数据。
     */
    suspend fun readNetworkUsage(): NetworkUsageInfoModel

    /**
     * 执行一次网络测速。
     *
     * @param onProgress 测速阶段与实时速度回调；可能在 IO 线程调用。
     * @return 最终测速结果。
     */
    suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel

    /** 最近一次局域网扫描结果；没有扫描时为 null。 */
    val lastScan: StateFlow<NetworkScanResultModel?>

    /**
     * 扫描当前局域网设备。
     *
     * @return 扫描结果；无网络或未授权时返回空结果。
     */
    suspend fun scanWifi(): NetworkScanResultModel

    /** 清空最近一次扫描结果。 */
    fun clearLastScan()
}
