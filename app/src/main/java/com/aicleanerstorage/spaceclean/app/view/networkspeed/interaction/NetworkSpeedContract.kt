package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedProgressModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkInfoModel

enum class NetworkSpeedPhase {
    Idle,
    Testing,
    Completing,
    Result,
    Error,
}

data class NetworkSpeedUiState(
    val networkInfo: NetworkInfoModel = NetworkInfoModel(),
    val hasNetwork: Boolean = false,
    val phase: NetworkSpeedPhase = NetworkSpeedPhase.Idle,
    val speed: NetworkSpeedResultModel? = null,
    val progress: NetworkSpeedProgressModel = NetworkSpeedProgressModel(),
    val errorMessage: String? = null,
) {
    val downloadLabel: String
        get() =
            when {
                progress.downloadMbps != null -> progress.downloadMbps
                speed != null -> speed.downloadMbps
                else -> "--"
            }

    val uploadLabel: String
        get() =
            when {
                progress.uploadMbps != null -> progress.uploadMbps
                speed != null -> speed.uploadMbps
                else -> "--"
            }

    val latencyLabel: String
        get() = progress.latencyMs?.toString() ?: speed?.latencyMs?.toString() ?: "--"
}

sealed interface NetworkSpeedAction {
    data class NavigationRequested(val destination: AppDestination) : NetworkSpeedAction
    data object BackClicked : NetworkSpeedAction
    data object NetworkRefreshRequested : NetworkSpeedAction
    data object SpeedTestRequested : NetworkSpeedAction
    data object ResultRequested : NetworkSpeedAction
    data object StopClicked : NetworkSpeedAction
}


sealed interface NetworkSpeedEvent {

    data class Exit(val home: Boolean) : NetworkSpeedEvent
    data class Navigate(val destination: AppDestination) : NetworkSpeedEvent
}
