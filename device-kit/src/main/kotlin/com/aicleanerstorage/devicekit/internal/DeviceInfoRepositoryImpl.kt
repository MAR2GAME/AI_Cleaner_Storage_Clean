package com.aicleanerstorage.devicekit.internal

import android.content.Context
import com.aicleanerstorage.devicekit.model.BatteryInfo
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.devicekit.model.MemoryInfo
import com.aicleanerstorage.devicekit.model.StorageInfo
import kotlinx.coroutines.CoroutineDispatcher

internal class DeviceInfoRepositoryImpl(
    context: Context,
    ioDispatcher: CoroutineDispatcher,
) : DeviceInfoRepository {
    private val appContext = context.applicationContext

    private val hardwareReader = DeviceHardwareReader(appContext)
    private val cpuUsageReader = ProcCpuUsageReader(ioDispatcher)

    override suspend fun readCpuUsagePercent(previousPercent: Int?) = cpuUsageReader.readUsagePercent(previousPercent)

    override fun batteryInfo(): BatteryInfo = DeviceInfoDataSource.getBatteryInfo(appContext)

    override fun memoryInfo(): MemoryInfo = DeviceInfoDataSource.getMemoryInfo(appContext)

    override fun internalStorageInfo(): StorageInfo = StorageDataSource.getInternalStorageInfo()

    override fun hardwareInfo() = hardwareReader.hardwareInfo()
    override fun batteryCurrentNowMa() = hardwareReader.batteryCurrentNowMa()
    override fun batteryCurrentAverageMa() = hardwareReader.batteryCurrentAverageMa()
    override fun cpuTemperatureC() = hardwareReader.cpuTemperatureC()
}
