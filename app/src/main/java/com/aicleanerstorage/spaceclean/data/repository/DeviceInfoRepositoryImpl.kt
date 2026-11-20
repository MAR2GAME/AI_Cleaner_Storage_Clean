package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.DeviceHardwareReader
import com.aicleanerstorage.spaceclean.data.local.DeviceInfoDataSource
import com.aicleanerstorage.spaceclean.data.local.ProcCpuUsageReader
import com.aicleanerstorage.spaceclean.data.local.StorageDataSource
import com.aicleanerstorage.spaceclean.domain.model.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.StorageInfoModel
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher

internal class DeviceInfoRepositoryImpl(
    context: Context,
    ioDispatcher: CoroutineDispatcher,
) : DeviceInfoRepository {
    private val appContext = context.applicationContext

    private val hardwareReader = DeviceHardwareReader(appContext)
    private val cpuUsageReader = ProcCpuUsageReader(ioDispatcher)

    override suspend fun readCpuUsagePercent(previousPercent: Int?) = cpuUsageReader.readUsagePercent(previousPercent)

    override fun batteryInfo(): BatteryInfoModel = DeviceInfoDataSource.getBatteryInfo(appContext)

    override fun batteryStatusInfo() = hardwareReader.batteryStatusInfo()

    override fun memoryInfo(): MemoryInfoModel = DeviceInfoDataSource.getMemoryInfo(appContext)

    override fun internalStorageInfo(): StorageInfoModel = StorageDataSource.getInternalStorageInfo()

    override fun hardwareInfo() = hardwareReader.hardwareInfo()
    override fun batteryCurrentNowMa() = hardwareReader.batteryCurrentNowMa()
    override fun batteryCurrentAverageMa() = hardwareReader.batteryCurrentAverageMa()
    override fun cpuTemperatureC() = hardwareReader.cpuTemperatureC()
}
