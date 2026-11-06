package com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.*

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.deviceinfo.CpuUsageReader
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.coroutines.runSuspendCatching
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySample
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryConfig
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySampler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt


class DeviceInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistorySampler,
    settingsRepository: SettingsRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    cpuUsageReader: CpuUsageReader,
) : MviViewModel<DeviceInfoUiState, DeviceInfoIntent, DeviceInfoEffect>(DeviceInfoUiState(mode = DeviceInfoMode.Device)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            batteryHistorySampler = batteryHistorySampler,
            settingsRepository = settingsRepository,
            scope = viewModelScope,
            ioDispatcher = ioDispatcher,
            cpuUsageReader = cpuUsageReader,
            initialMode = DeviceInfoMode.Device,
            currentState = { currentState },
            publishState = { dispatch(DeviceInfoStateChanged(it)) },
        )


    override fun onIntent(action: DeviceInfoIntent) {
        when (action) {
            is DeviceInfoStateChanged -> updateState { DeviceInfoReducer.reduce(this, action) }
            DeviceInfoIntent.Resumed -> controller.load(DeviceInfoMode.Device)
            DeviceInfoIntent.Back -> if (currentState.isScanning) controller.showStopDialog() else exit()
            DeviceInfoIntent.QuitScan -> { controller.dismissStopDialog(); exit() }
            DeviceInfoIntent.ResumeScan -> controller.resumeScan()
            else -> Unit
        }
    }

    private fun exit() {
        emitEffect(DeviceInfoEffect.Exit)
    }

    override fun onCleared() {
        controller.close()
        super.onCleared()
    }
}
