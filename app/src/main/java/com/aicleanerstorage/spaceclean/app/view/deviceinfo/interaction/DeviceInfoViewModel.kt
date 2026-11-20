package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.*

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.usecase.SampleBatteryHistoryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeviceInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    sampleBatteryHistory: SampleBatteryHistoryUseCase,
    settingsRepository: SettingsRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<DeviceInfoUiState, DeviceInfoAction, DeviceInfoEvent>(DeviceInfoUiState(mode = DeviceInfoMode.Device)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            sampleBatteryHistory = sampleBatteryHistory,
            settingsRepository = settingsRepository,
            scope = viewModelScope,
            ioDispatcher = ioDispatcher,
            initialMode = DeviceInfoMode.Device,
            currentState = { currentState },
            changeState = ::changeState,
        )

    override fun onAction(action: DeviceInfoAction) {
        when (action) {
            DeviceInfoAction.Resumed -> controller.load(DeviceInfoMode.Device)
            DeviceInfoAction.BackClicked -> if (currentState.isScanning) controller.showStopDialog() else exit()
            DeviceInfoAction.QuitScanConfirmed -> { controller.dismissStopDialog(); exit() }
            DeviceInfoAction.ResumeScanClicked -> controller.resumeScan()
            else -> Unit
        }
    }

    private fun exit() {
        sendEvent(DeviceInfoEvent.Exit)
    }

    override fun onCleared() {
        controller.close()
        super.onCleared()
    }
}
