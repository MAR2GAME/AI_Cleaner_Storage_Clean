package com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation

import com.aicleanerstorage.spaceclean.core.contracts.device.StorageInfo

data class OnboardingUiState(
    val deviceModel: String = "",
    val androidVersion: String = "",
    val screenSize: String = "",
    val batteryHealth: String = "",
    val batteryStatusText: String = "",
    val storageInfo: StorageInfo = StorageInfo(0, 0, 0),
    val completedStep: Int = 0,
) {
    val complete: Boolean get() = completedStep > ONBOARDING_SCAN_ROW_COUNT
}

sealed interface OnboardingIntent {
    data object Entered : OnboardingIntent
    data object Refresh : OnboardingIntent
    data object Skip : OnboardingIntent
    data object GetStarted : OnboardingIntent


}

sealed interface OnboardingEffect {
    data object ScanFinished : OnboardingEffect
    data class Complete(val skipped: Boolean) : OnboardingEffect
}

internal data class OnboardingLoaded(val value: OnboardingUiState) : OnboardingIntent
internal data class OnboardingProgress(val step: Int) : OnboardingIntent
internal const val ONBOARDING_SCAN_ROW_COUNT = 6
