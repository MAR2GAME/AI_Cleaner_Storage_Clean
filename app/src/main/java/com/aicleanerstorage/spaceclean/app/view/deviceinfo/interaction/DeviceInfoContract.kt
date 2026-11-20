package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.*

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.domain.model.BatteryStatusInfoModel
import com.aicleanerstorage.spaceclean.domain.model.DeviceCpuInfoModel
import com.aicleanerstorage.spaceclean.domain.model.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.DeviceSensorInfoModel
import com.aicleanerstorage.spaceclean.domain.model.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.StorageInfoModel
import kotlin.math.abs

sealed interface DeviceInfoAction {
    data object Resumed : DeviceInfoAction
    data object BackClicked : DeviceInfoAction
    data object QuitScanConfirmed : DeviceInfoAction
    data object ResumeScanClicked : DeviceInfoAction

}

sealed interface DeviceInfoEvent {
    data object Exit : DeviceInfoEvent
}
