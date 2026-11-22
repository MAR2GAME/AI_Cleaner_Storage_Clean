package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel

/**
 * 设备信息读取契约；只读，不缓存，调用方按页面生命周期自行刷新。
 *
 * 读取失败时返回 null 或空模型，不向页面抛平台异常。
 */
interface DeviceInfoRepository {
    /**
     * 读取两次调用之间的 CPU 占用率。
     *
     * @param previousPercent 上一次占用率；首次读取传 null。
     * @return 区间占用率；无法读取时返回 null。
     */
    suspend fun readCpuUsagePercent(previousPercent: Int?): Int?

    /**
     * 读取电池信息。
     *
     * @return 当前电池快照；系统不可用时返回未知值模型。
     */
    fun batteryInfo(): BatteryInfoModel

    /**
     * 读取内存容量。
     *
     * @return 内存总量与可用量。
     */
    fun memoryInfo(): CapacityInfoModel

    /**
     * 读取内部存储容量。
     *
     * @return 存储总量与可用量。
     */
    fun internalStorageInfo(): CapacityInfoModel

    /**
     * 读取硬件信息。
     *
     * @return CPU、屏幕、传感器等静态信息。
     */
    fun hardwareInfo(): DeviceHardwareInfoModel

    /**
     * 读取 CPU 温度。
     *
     * @return 摄氏度；设备不提供时返回 null。
     */
    fun cpuTemperatureC(): Float?

    /**
     * 读取当前电池电流。
     *
     * @return 毫安值；设备不提供时返回 null。
     */
    fun batteryCurrentNowMa(): Float?

    /**
     * 读取电池平均电流。
     *
     * @return 毫安值；设备不提供时返回 null。
     */
    fun batteryCurrentAverageMa(): Float?
}
