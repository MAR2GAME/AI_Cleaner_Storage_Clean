package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.*
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeviceInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistoryRecorder,
    settingsRepository: AppSettingsRepository,
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
            setState = ::setState,
        )

    override fun onAction(action: DeviceInfoAction) {
        when (action) {
            DeviceInfoAction.Resumed -> controller.load(DeviceInfoMode.Device)
            DeviceInfoAction.BackClicked -> if (currentState.isScanning) controller.showStopDialog() else exit()
            DeviceInfoAction.QuitScanConfirmed -> {
                controller.dismissStopDialog()
                exit()
            }
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
