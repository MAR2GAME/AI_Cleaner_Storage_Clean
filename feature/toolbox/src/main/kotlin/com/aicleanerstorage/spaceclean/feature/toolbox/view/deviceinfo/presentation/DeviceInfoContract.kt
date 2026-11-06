package com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.*

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryStatusInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceCpuInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceHardwareInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceSensorInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.MemoryInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.StorageInfo
import kotlin.math.abs

sealed interface DeviceInfoIntent {
    data object Resumed : DeviceInfoIntent
    data object Back : DeviceInfoIntent
    data object QuitScan : DeviceInfoIntent
    data object ResumeScan : DeviceInfoIntent

}

sealed interface DeviceInfoEffect {
    data object Exit : DeviceInfoEffect
}

internal data class DeviceInfoStateChanged(val value: DeviceInfoUiState) : DeviceInfoIntent


