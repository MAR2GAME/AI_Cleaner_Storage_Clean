package com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation

import com.aicleanerstorage.spaceclean.core.contracts.onboarding.OnboardingPreferences
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class OnboardingScanViewModel(
    private val repository: DeviceInfoRepository,
    private val onboardingPreferences: OnboardingPreferences,
) : MviViewModel<OnboardingUiState, OnboardingIntent, OnboardingEffect>(OnboardingUiState()) {
    private var entered = false
    private var completionSubmitted = false

    init {
        refresh()
    }

    override fun onIntent(action: OnboardingIntent) {
        when (action) {
            OnboardingIntent.Entered -> startScanProgress()
            OnboardingIntent.Refresh -> refresh()
            OnboardingIntent.Skip -> completeOnboarding(skipped = true)
            OnboardingIntent.GetStarted -> completeOnboarding(skipped = false)
            is OnboardingLoaded, is OnboardingProgress -> updateState { OnboardingReducer.reduce(this, action) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val hardware = repository.hardwareInfo()
            val battery = repository.batteryInfo()
            val batteryStatus = repository.batteryStatusInfo()
            val storage = repository.internalStorageInfo()
            dispatch(OnboardingLoaded(currentState.copy(
                deviceModel = hardware.model, androidVersion = hardware.androidVersion,
                screenSize = hardware.screenSize, batteryHealth = battery.health,
                batteryStatusText = batteryStatus.statusText, storageInfo = storage,
            )))
        }
    }

    private fun startScanProgress() {
        if (entered) return
        entered = true
        viewModelScope.launch {
            for (step in 1..ONBOARDING_SCAN_ROW_COUNT) {
                delay(ONBOARDING_STEP_DELAY_MILLIS)
                dispatch(OnboardingProgress(step))
            }
            delay(ONBOARDING_STEP_DELAY_MILLIS)
            dispatch(OnboardingProgress(ONBOARDING_SCAN_ROW_COUNT + 1))
            emitEffect(OnboardingEffect.ScanFinished)
        }
    }

    private fun completeOnboarding(skipped: Boolean) {
        if (completionSubmitted || !currentState.complete) return
        completionSubmitted = true
        onboardingPreferences.markScanCompleted()
        emitEffect(OnboardingEffect.Complete(skipped))
    }
}

private const val ONBOARDING_STEP_DELAY_MILLIS = 680L
