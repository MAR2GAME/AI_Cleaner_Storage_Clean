package com.aicleanerstorage.spaceclean.app.view.battery.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoMode
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoStateController
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.BatterySamplingControl
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BatteryInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistoryRecorder,
    settingsRepository: AppSettingsRepository,
    private val batterySamplingControl: BatterySamplingControl,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<DeviceInfoUiState, BatteryInfoAction, BatteryInfoEvent>(DeviceInfoUiState(mode = DeviceInfoMode.Battery)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            batteryHistorySampler = batteryHistorySampler,
            settingsRepository = settingsRepository,
            scope = viewModelScope,
            ioDispatcher = ioDispatcher,
            initialMode = DeviceInfoMode.Battery,
            currentState = { currentState },
            setState = ::setState,
        )

    override fun onAction(action: BatteryInfoAction) {
        when (action) {
            BatteryInfoAction.Resumed -> {
                batterySamplingControl.activate()
                controller.load(DeviceInfoMode.Battery)
                controller.startBatterySampling()
            }
            BatteryInfoAction.Paused -> controller.stopBatterySampling()
            BatteryInfoAction.BackClicked -> {
                if (currentState.isScanning) controller.showStopDialog() else exit()
            }
            BatteryInfoAction.QuitScanConfirmed -> {
                controller.dismissStopDialog()
                exit()
            }
            BatteryInfoAction.ResumeScanClicked -> controller.resumeScan()
            is BatteryInfoAction.RangeSelected -> controller.selectCurrentRange(action.range)
        }
    }

    private fun exit() {
        sendEvent(BatteryInfoEvent.Exit)
    }

    override fun onCleared() {
        controller.close()
        super.onCleared()
    }
}
