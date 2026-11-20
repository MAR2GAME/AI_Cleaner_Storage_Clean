package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import java.util.Locale

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.networkkit.model.NetworkSpeed
import com.aicleanerstorage.networkkit.model.NetworkInfo

enum class NetworkSpeedPhase {
    Idle,
    Testing,
    Completing,
    Result,
    Error,
}

data class NetworkSpeedUiState(
    val networkInfo: NetworkInfo = NetworkInfo(),
    val hasNetwork: Boolean = false,
    val phase: NetworkSpeedPhase = NetworkSpeedPhase.Idle,
    val speed: NetworkSpeed = NetworkSpeed(),
    val errorMessage: String? = null,
) {
    val downloadLabel: String
        get() = speed.downloadMbps.formatMbps()

    val uploadLabel: String
        get() = speed.uploadMbps.formatMbps()
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

internal fun Double?.formatMbps(): String = this?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "--"
