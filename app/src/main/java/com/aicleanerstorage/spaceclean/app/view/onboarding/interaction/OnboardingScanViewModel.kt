package com.aicleanerstorage.spaceclean.app.view.onboarding.interaction

import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class OnboardingScanViewModel(
    private val repository: DeviceInfoRepository,
    private val settingsRepository: SettingsRepository,
) : MviViewModel<OnboardingUiState, OnboardingAction, OnboardingEvent>(OnboardingUiState()) {
    private var entered = false
    private var completionSubmitted = false

    init {
        refresh()
    }

    override fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.Entered -> startScanProgress()
            OnboardingAction.RefreshRequested -> refresh()
            OnboardingAction.SkipClicked -> completeOnboarding(skipped = true)
            OnboardingAction.GetStartedClicked -> completeOnboarding(skipped = false)
            is OnboardingLoaded, is OnboardingProgressUpdated -> setState { OnboardingReducer.reduce(this, action) }
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
                dispatch(OnboardingProgressUpdated(step))
            }
            delay(ONBOARDING_STEP_DELAY_MILLIS)
            dispatch(OnboardingProgressUpdated(ONBOARDING_SCAN_ROW_COUNT + 1))
            sendEvent(OnboardingEvent.NotifyScanFinished)
        }
    }

    private fun completeOnboarding(skipped: Boolean) {
        if (completionSubmitted || !currentState.complete) return
        completionSubmitted = true
        settingsRepository.markOnboardingScanCompleted()
        sendEvent(OnboardingEvent.FinishOnboarding(skipped))
    }
}

private const val ONBOARDING_STEP_DELAY_MILLIS = 680L
