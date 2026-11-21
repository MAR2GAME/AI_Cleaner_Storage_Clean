package com.aicleanerstorage.spaceclean.app.view.onboarding.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            OnboardingAction.GetStartedClicked -> completeOnboarding()
            is OnboardingLoaded, is OnboardingProgressUpdated -> setState { OnboardingReducer.reduce(it, action) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val hardware = repository.hardwareInfo()
            val battery = repository.batteryInfo()
            val storage = repository.internalStorageInfo()
            dispatch(
                OnboardingLoaded(
                    currentState.copy(
                        deviceModel = hardware.model,
                        androidVersion = hardware.androidVersion,
                        screenSize = hardware.screenSize,
                        batteryHealth = battery.health,
                        batteryStatusText = battery.statusText,
                        storageInfo = storage,
                    ),
                ),
            )
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

    private fun completeOnboarding() {
        if (completionSubmitted || !currentState.complete) return
        completionSubmitted = true
        settingsRepository.markOnboardingScanCompleted()
        sendEvent(OnboardingEvent.FinishOnboarding)
    }
}

private const val ONBOARDING_STEP_DELAY_MILLIS = 680L
