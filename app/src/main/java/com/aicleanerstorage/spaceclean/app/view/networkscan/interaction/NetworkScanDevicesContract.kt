package com.aicleanerstorage.spaceclean.app.view.networkscan.interaction

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText

/** Devices view derived from [NetworkScanUiState]; no separate page state. */
data class NetworkScanDevicesUiState(
    val scan: NetworkScanResultModel? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null,
) {
    val devices get() = scan?.devices.orEmpty()
}
