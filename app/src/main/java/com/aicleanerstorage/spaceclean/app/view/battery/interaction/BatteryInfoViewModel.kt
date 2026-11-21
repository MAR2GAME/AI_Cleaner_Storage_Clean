package com.aicleanerstorage.spaceclean.app.view.battery.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoMode
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoStateController
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.battery.usecase.SyncBackgroundBatterySamplingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BatteryInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    batteryHistorySampler: BatteryHistoryRecorder,
    settingsRepository: SettingsRepository,
    private val syncBackgroundBatterySampling: SyncBackgroundBatterySamplingUseCase,
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
                syncBackgroundBatterySampling.activate()
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
