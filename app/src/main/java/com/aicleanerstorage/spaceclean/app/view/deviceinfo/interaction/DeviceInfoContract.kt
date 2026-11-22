package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction


sealed interface DeviceInfoAction {
    data object Resumed : DeviceInfoAction

    data object BackClicked : DeviceInfoAction

    data object QuitScanConfirmed : DeviceInfoAction

    data object ResumeScanClicked : DeviceInfoAction
}

sealed interface DeviceInfoEvent {
    data object Exit : DeviceInfoEvent
}
