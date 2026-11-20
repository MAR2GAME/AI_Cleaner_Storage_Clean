package com.aicleanerstorage.spaceclean.app.view.battery.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.usecase.SampleBatteryHistoryUseCase
import com.aicleanerstorage.spaceclean.domain.usecase.SyncBackgroundBatterySamplingUseCase
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoMode
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoStateController
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BatteryInfoViewModel(
    repository: DeviceInfoRepository,
    batteryHistoryRepository: BatteryHistoryRepository,
    sampleBatteryHistory: SampleBatteryHistoryUseCase,
    settingsRepository: SettingsRepository,
    private val syncBackgroundBatterySampling: SyncBackgroundBatterySamplingUseCase,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<DeviceInfoUiState, BatteryInfoAction, BatteryInfoEvent>(DeviceInfoUiState(mode = DeviceInfoMode.Battery)) {
    private val controller =
        DeviceInfoStateController(
            repository = repository,
            batteryHistoryRepository = batteryHistoryRepository,
            sampleBatteryHistory = sampleBatteryHistory,
            settingsRepository = settingsRepository,
            scope = viewModelScope,
            ioDispatcher = ioDispatcher,
            initialMode = DeviceInfoMode.Battery,
            currentState = { currentState },
            changeState = ::changeState,
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

