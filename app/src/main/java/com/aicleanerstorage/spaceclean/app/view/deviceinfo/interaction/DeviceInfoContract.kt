package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.*

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.devicekit.model.DeviceCpuInfo
import com.aicleanerstorage.devicekit.model.DeviceHardwareInfo
import com.aicleanerstorage.devicekit.model.DeviceSensorInfo
import com.aicleanerstorage.devicekit.model.BatteryInfo
import com.aicleanerstorage.devicekit.model.MemoryInfo
import com.aicleanerstorage.devicekit.model.StorageInfo
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
