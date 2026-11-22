package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel

/**
 * 设备信息读取契约；只读，不缓存，调用方按页面生命周期自行刷新。
 *
 * [readCpuUsagePercent] 的 [previousPercent] 用于计算区间占用率，第一次读取传 null。
 */
interface DeviceInfoRepository {
    suspend fun readCpuUsagePercent(previousPercent: Int?): Int?

    fun batteryInfo(): BatteryInfoModel

    fun memoryInfo(): CapacityInfoModel

    fun internalStorageInfo(): CapacityInfoModel

    fun hardwareInfo(): DeviceHardwareInfoModel

    fun cpuTemperatureC(): Float?

    fun batteryCurrentNowMa(): Float?

    fun batteryCurrentAverageMa(): Float?
}
