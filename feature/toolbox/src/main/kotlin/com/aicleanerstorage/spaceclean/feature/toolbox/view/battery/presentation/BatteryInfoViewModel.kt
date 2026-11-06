package com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingController
import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.DeviceInfoMode
import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.DeviceInfoStateController
import com.aicleanerstorage.spaceclean.feature.toolbox.view.shared.presentation.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.deviceinfo.CpuUsageReader
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BatteryInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistorySampler,
    settingsRepository: SettingsRepository,
    private val backgroundBatterySamplingController: BackgroundBatterySamplingController,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    cpuUsageReader: CpuUsageReader,
) : MviViewModel<DeviceInfoUiState, BatteryInfoIntent, BatteryInfoEffect>(DeviceInfoUiState(mode = DeviceInfoMode.Battery)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            batteryHistorySampler = batteryHistorySampler,
            settingsRepository = settingsRepository,
            scope = viewModelScope,
            ioDispatcher = ioDispatcher,
            cpuUsageReader = cpuUsageReader,
            initialMode = DeviceInfoMode.Battery,
            currentState = { currentState },
            publishState = { dispatch(BatteryInfoStateChanged(it)) },
        )


    override fun onIntent(action: BatteryInfoIntent) {
        when (action) {
            is BatteryInfoStateChanged -> updateState { BatteryInfoReducer.reduce(this, action) }
            BatteryInfoIntent.Resumed -> {
                backgroundBatterySamplingController.activate()
                controller.load(DeviceInfoMode.Battery)
                controller.startBatterySampling()
            }
            BatteryInfoIntent.Paused -> controller.stopBatterySampling()
            BatteryInfoIntent.Back -> {
                if (currentState.isScanning) controller.showStopDialog() else exit()
            }
            BatteryInfoIntent.QuitScan -> {
                controller.dismissStopDialog()
                exit()
            }
            BatteryInfoIntent.ResumeScan -> controller.resumeScan()
            is BatteryInfoIntent.RangeSelected -> controller.selectCurrentRange(action.range)
        }
    }

    private fun exit() {
        emitEffect(BatteryInfoEffect.Exit)
    }

    override fun onCleared() {
        controller.close()
        super.onCleared()
    }
}

