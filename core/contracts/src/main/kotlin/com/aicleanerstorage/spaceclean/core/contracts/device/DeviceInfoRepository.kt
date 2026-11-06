package com.aicleanerstorage.spaceclean.core.contracts.device

import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryStatusInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceHardwareInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.MemoryInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.StorageInfo

interface DeviceInfoRepository {
    fun batteryInfo(): BatteryInfo

    fun batteryStatusInfo(): BatteryStatusInfo

    fun memoryInfo(): MemoryInfo

    fun internalStorageInfo(): StorageInfo

    fun hardwareInfo(): DeviceHardwareInfo

    fun cpuTemperatureC(): Float?

    fun batteryCurrentNowMa(): Float?

    fun batteryCurrentAverageMa(): Float?
}
