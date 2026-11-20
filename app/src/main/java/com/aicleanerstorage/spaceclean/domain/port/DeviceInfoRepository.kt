package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.BatteryStatusInfoModel
import com.aicleanerstorage.spaceclean.domain.model.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.StorageInfoModel

interface DeviceInfoRepository {
    suspend fun readCpuUsagePercent(previousPercent: Int?): Int?

    fun batteryInfo(): BatteryInfoModel

    fun batteryStatusInfo(): BatteryStatusInfoModel

    fun memoryInfo(): MemoryInfoModel

    fun internalStorageInfo(): StorageInfoModel

    fun hardwareInfo(): DeviceHardwareInfoModel

    fun cpuTemperatureC(): Float?

    fun batteryCurrentNowMa(): Float?

    fun batteryCurrentAverageMa(): Float?
}
