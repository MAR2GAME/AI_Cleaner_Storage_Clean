package com.aicleanerstorage.spaceclean.app.view.battery.interaction

import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.BatteryCurrentRange

sealed interface BatteryInfoAction {
    data object Resumed : BatteryInfoAction

    data object Paused : BatteryInfoAction

    data object BackClicked : BatteryInfoAction

    data object QuitScanConfirmed : BatteryInfoAction

    data object ResumeScanClicked : BatteryInfoAction

    data class RangeSelected(
        val range: BatteryCurrentRange,
    ) : BatteryInfoAction
}

sealed interface BatteryInfoEvent {
    data object Exit : BatteryInfoEvent
}
