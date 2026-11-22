package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.deviceinfo.DeviceHardwareReader
import com.aicleanerstorage.spaceclean.data.local.deviceinfo.DeviceInfoDataSource
import com.aicleanerstorage.spaceclean.data.local.deviceinfo.ProcCpuUsageReader
import com.aicleanerstorage.spaceclean.data.local.deviceinfo.StorageDataSource
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher

/** 设备信息读取的唯一实现，直接实现 domain 端口。 */
internal class DefaultDeviceInfoRepository(
    context: Context,
    ioDispatcher: CoroutineDispatcher,
) : DeviceInfoRepository {
    private val appContext = context.applicationContext

    private val hardwareReader = DeviceHardwareReader(appContext)
    private val cpuUsageReader = ProcCpuUsageReader(ioDispatcher)

    override suspend fun readCpuUsagePercent(previousPercent: Int?): Int? = cpuUsageReader.readUsagePercent(previousPercent)

    override fun batteryInfo(): BatteryInfoModel = DeviceInfoDataSource.getBatteryInfo(appContext)

    override fun memoryInfo(): CapacityInfoModel = DeviceInfoDataSource.getMemoryInfo(appContext)

    override fun internalStorageInfo(): CapacityInfoModel = StorageDataSource.getInternalStorageInfo()

    override fun hardwareInfo(): DeviceHardwareInfoModel = hardwareReader.hardwareInfo()

    override fun batteryCurrentNowMa(): Float? = hardwareReader.batteryCurrentNowMa()

    override fun batteryCurrentAverageMa(): Float? = hardwareReader.batteryCurrentAverageMa()

    override fun cpuTemperatureC(): Float? = hardwareReader.cpuTemperatureC()
}
