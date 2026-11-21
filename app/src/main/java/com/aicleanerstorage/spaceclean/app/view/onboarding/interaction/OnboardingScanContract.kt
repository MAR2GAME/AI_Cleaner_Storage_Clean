package com.aicleanerstorage.spaceclean.app.view.onboarding.interaction

import com.aicleanerstorage.devicekit.model.StorageInfo

data class OnboardingUiState(
    val deviceModel: String = "",
    val androidVersion: String = "",
    val screenSize: String = "",
    val batteryHealth: String = "",
    val batteryStatusText: String = "",
    val storageInfo: StorageInfo = StorageInfo(0, 0),
    val completedStep: Int = 0,
) {
    val complete: Boolean get() = completedStep > ONBOARDING_SCAN_ROW_COUNT
}

sealed interface OnboardingAction {
    data object Entered : OnboardingAction

    data object RefreshRequested : OnboardingAction

    data object GetStartedClicked : OnboardingAction
}

sealed interface OnboardingEvent {
    data object NotifyScanFinished : OnboardingEvent

    data object FinishOnboarding : OnboardingEvent
}

internal data class OnboardingLoaded(
    val value: OnboardingUiState,
) : OnboardingAction

internal data class OnboardingProgressUpdated(
    val step: Int,
) : OnboardingAction

internal const val ONBOARDING_SCAN_ROW_COUNT = 6
