package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import java.util.Locale

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
    val speed: NetworkSpeedModel = NetworkSpeedModel(),
) {
    /** 单一真相：错误文案由 phase 派生。 */
    val error: UiText?
        get() = if (phase == NetworkSpeedPhase.Error) UiText.Res(R.string.speed_test_failed) else null

    val downloadLabel: String
        get() = speed.downloadMbps.formatMbps()

    val uploadLabel: String
        get() = speed.uploadMbps.formatMbps()
}

sealed interface NetworkSpeedAction {
    data class NavigationRequested(
        val destination: AppDestination,
    ) : NetworkSpeedAction

    data object BackClicked : NetworkSpeedAction

    data object NetworkRefreshRequested : NetworkSpeedAction

    data object SpeedTestRequested : NetworkSpeedAction

    data object ResultRequested : NetworkSpeedAction

    data object StopClicked : NetworkSpeedAction
}

sealed interface NetworkSpeedEvent {
    data object Exit : NetworkSpeedEvent

    data class Navigate(
        val destination: AppDestination,
    ) : NetworkSpeedEvent
}

internal fun Double?.formatMbps(): String = this?.let { String.format(Locale.US, "%.1f", it) } ?: "--"
