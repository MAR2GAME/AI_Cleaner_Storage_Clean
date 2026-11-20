package com.aicleanerstorage.devicekit

import com.aicleanerstorage.devicekit.model.BatteryInfo
import com.aicleanerstorage.devicekit.model.DeviceHardwareInfo
import com.aicleanerstorage.devicekit.model.MemoryInfo
import com.aicleanerstorage.devicekit.model.StorageInfo

interface DeviceInfoRepository {
    suspend fun readCpuUsagePercent(previousPercent: Int?): Int?

    fun batteryInfo(): BatteryInfo

    fun memoryInfo(): MemoryInfo

    fun internalStorageInfo(): StorageInfo

    fun hardwareInfo(): DeviceHardwareInfo

    fun cpuTemperatureC(): Float?

    fun batteryCurrentNowMa(): Float?

    fun batteryCurrentAverageMa(): Float?
}
