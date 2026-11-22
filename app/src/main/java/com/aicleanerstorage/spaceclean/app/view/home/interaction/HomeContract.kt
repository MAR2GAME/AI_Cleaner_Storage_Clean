package com.aicleanerstorage.spaceclean.app.view.home.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel

data class HomeSummaryUiState(
    val storageInfo: CapacityInfoModel = CapacityInfoModel(0, 0),
    val batteryInfo: BatteryInfoModel = BatteryInfoModel(-1),
    val deviceModel: String = "Unknown",
    val androidVersion: String = "Android --",
    val lockedAppCount: Int = 0,
    val isLoading: Boolean = true,
)

data class HomeUiState(
    val selectedTab: Int = 0,
    val summary: HomeSummaryUiState = HomeSummaryUiState(),
    val exitPromptDestination: AppDestination? = null,
    val showAutoRateDialog: Boolean = false,
    val externalBlockingPromptActive: Boolean = false,
)

sealed interface HomeAction {
    data class Entered(
        val initialTab: Int,
    ) : HomeAction

    data class TabSelected(
        val index: Int,
    ) : HomeAction

    data class NavigationRequested(
        val destination: AppDestination,
    ) : HomeAction

    data object ExitClicked : HomeAction

    data object ExitDismissed : HomeAction

    data object AutoRateDismissed : HomeAction

    data object FeatureClicked : HomeAction

    data object TabInteracted : HomeAction

    data object SummaryRefreshRequested : HomeAction

    data class ExternalBlockingPromptChanged(
        val active: Boolean,
    ) : HomeAction

    data object ExitConfirmed : HomeAction

    data object RateAppClicked : HomeAction

    data object ExitPromptViewClicked : HomeAction
}

sealed interface HomeEvent {
    data object ExitApp : HomeEvent

    data object RateApp : HomeEvent

    data class Navigate(
        val destination: AppDestination,
    ) : HomeEvent
}
