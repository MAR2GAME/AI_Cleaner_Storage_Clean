package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.*

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistoryRepository
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeviceInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistorySampler,
    settingsRepository: SettingsRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<DeviceInfoUiState, DeviceInfoAction, DeviceInfoEvent>(DeviceInfoUiState(mode = DeviceInfoMode.Device)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            batteryHistorySampler = batteryHistorySampler,
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
