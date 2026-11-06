package com.aicleanerstorage.spaceclean.feature.home.view.home.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryInfo
import com.aicleanerstorage.spaceclean.core.contracts.device.StorageInfo

data class HomeSummaryUiState(
    val storageInfo: StorageInfo = StorageInfo(0, 0, 0),
    val batteryInfo: BatteryInfo = BatteryInfo(-1, "Unknown", 0f, -1, "Unknown", 0, "Unknown"),
    val batteryStatusText: String = "Unknown",
    val deviceModel: String = "Unknown",
    val androidVersion: String = "Android --",
    val lockedAppCount: Int = 0,
    val isLoading: Boolean = true,
)

data class HomeUiState(
    val selectedTab: Int = 0,
    val summary: HomeSummaryUiState = HomeSummaryUiState(),
    val exitPromptRoute: String? = null,
    val showAutoRateDialog: Boolean = false,
    val externalBlockingPromptActive: Boolean = false,
)

sealed interface HomeIntent {
    data class Entered(val initialTab: Int) : HomeIntent
    data class TabSelected(val index: Int) : HomeIntent
    data class Navigate(val destination: AppDestination) : HomeIntent
    data object RequestExitPrompt : HomeIntent
    data object DismissExitPrompt : HomeIntent
    data object DismissAutoRateDialog : HomeIntent
    data object FeatureClicked : HomeIntent
    data object TabInteracted : HomeIntent
    data object RefreshSummary : HomeIntent
    data class ExternalBlockingPromptChanged(val active: Boolean) : HomeIntent
    data object ExitApp : HomeIntent
    data object RateApp : HomeIntent

    data object OnFeatureClicked : HomeIntent
    data class SetExternalBlockingPromptActive(val active: Boolean) : HomeIntent
    data object OnTabInteraction : HomeIntent
}

sealed interface HomeEffect {
    data object ExitApp : HomeEffect
    data object RateApp : HomeEffect
    data class Navigate(val destination: AppDestination) : HomeEffect
}

internal data class HomeStateChanged(val value: HomeUiState) : HomeIntent
