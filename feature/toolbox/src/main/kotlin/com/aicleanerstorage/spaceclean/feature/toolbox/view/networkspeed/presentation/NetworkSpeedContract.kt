package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedProgress
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedResult
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkInfo

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
    val speed: NetworkSpeedResult? = null,
    val progress: NetworkSpeedProgress = NetworkSpeedProgress(),
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

sealed interface NetworkSpeedIntent {
    data class Navigate(val destination: AppDestination) : NetworkSpeedIntent
    data object Back : NetworkSpeedIntent
    data object RefreshNetworkStateUntilNetworkAvailable : NetworkSpeedIntent
    data object RunSpeedTest : NetworkSpeedIntent
    data object ShowResult : NetworkSpeedIntent
    data object StopSpeedTest : NetworkSpeedIntent
}

internal data class NetworkSpeedStateChanged(val value: NetworkSpeedUiState) : NetworkSpeedIntent

sealed interface NetworkSpeedEffect {
    data class Operation(val event: FeatureOperationEvent) : NetworkSpeedEffect
    data class Exit(val home: Boolean) : NetworkSpeedEffect
    data class Navigate(val destination: AppDestination) : NetworkSpeedEffect
}

