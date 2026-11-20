package com.aicleanerstorage.spaceclean.app.view.home.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

private const val AUTO_RATE_PROMPT_DELAY_MS = 15_000L
private const val AUTO_RATE_PROMPT_COOLDOWN_MS = 24L * 60 * 60 * 1000

class HomeViewModel(
    private val repository: DeviceInfoRepository,
    private val appLockRepository: AppLockRepository,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val autoRateDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val autoRatePromptDelayMillis: Long = AUTO_RATE_PROMPT_DELAY_MS,
) : MviViewModel<HomeUiState, HomeAction, HomeEvent>(HomeUiState()) {

    private var autoRatePromptJob: Job? = null
    private var entered = false
    private var featureClicked = false
    private var externalBlockingPromptActive = false

    init {
        refreshSummary()
        scheduleAutoRatePrompt()
        viewModelScope.launch {
            appLockRepository.lockedAppCountFlow.collect { count ->
                changeState { it.copy(summary = it.summary.copy(lockedAppCount = count)) }
            }
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ExitClicked -> requestExitPrompt()
            HomeAction.ExitDismissed -> dismissExitPrompt()
            HomeAction.AutoRateDismissed -> dismissAutoRateDialog()
            HomeAction.FeatureClicked -> onFeatureClicked()
            HomeAction.TabInteracted -> onTabInteraction()
            HomeAction.SummaryRefreshRequested -> refreshSummary()
            is HomeAction.ExternalBlockingPromptChanged -> setExternalBlockingPromptActive(action.active)
            is HomeAction.Entered -> if (!entered) { entered = true; selectTab(action.initialTab) }
            is HomeAction.TabSelected -> { entered = true; onTabInteraction(); selectTab(action.index) }
            is HomeAction.NavigationRequested -> { onFeatureClicked(); sendEvent(HomeEvent.Navigate(action.destination)) }
            HomeAction.ExitConfirmed -> sendEvent(HomeEvent.ExitApp)
            HomeAction.RateAppClicked -> sendEvent(HomeEvent.RateApp)
        }
    }

    private fun selectTab(index: Int) {
        changeState { it.copy(selectedTab = index.coerceIn(0, 2)) }
    }

    private fun requestExitPrompt() {
        if (currentState.exitPromptDestination != null) return
        autoRatePromptJob?.cancel()
        val destination = nextExitPromptDestination()
        changeState { it.copy(exitPromptDestination = destination) }
    }

    private fun dismissExitPrompt() {
        changeState { it.copy(exitPromptDestination = null) }
    }

    private fun dismissAutoRateDialog() {
        changeState { it.copy(showAutoRateDialog = false) }
    }

    private fun onFeatureClicked() {
        featureClicked = true
        autoRatePromptJob?.cancel()
        changeState { it.copy(showAutoRateDialog = false) }
    }

    private fun setExternalBlockingPromptActive(active: Boolean) {
        if (externalBlockingPromptActive == active) return
        externalBlockingPromptActive = active
        changeState { it.copy(externalBlockingPromptActive = active) }
        if (active) {
            autoRatePromptJob?.cancel()
            changeState { it.copy(showAutoRateDialog = false) }
            return
        }
        scheduleAutoRatePrompt()
    }

    private fun onTabInteraction() {
        onFeatureClicked()
    }

    fun consumeExitPromptForNavigation(): AppDestination? {
        val route = currentState.exitPromptDestination ?: return null
        onFeatureClicked()
        changeState { it.copy(exitPromptDestination = null) }
        return route
    }

    private fun refreshSummary() {
        viewModelScope.launch(ioDispatcher) {
            val currentSummary = currentState.summary
            val loadedState =
                runCatching {
                    val hardware = repository.hardwareInfo()
                    HomeSummaryUiState(
                        storageInfo = repository.internalStorageInfo(),
                        batteryInfo = repository.batteryInfo(),
                        batteryStatusText = repository.batteryStatusInfo().statusText,
                        deviceModel = hardware.model,
                        androidVersion = hardware.androidVersion,
                        lockedAppCount = runCatching { appLockRepository.lockedAppCountFlow.value }.getOrDefault(0),
                        isLoading = false,
                    )
                }.getOrElse {
                    currentSummary.copy(isLoading = false)
                }
            changeState { it.copy(summary = loadedState) }
        }
    }

    private fun nextExitPromptDestination(): AppDestination {
        val notificationCleaner = AppDestination.NotificationCleaner
        if (!settingsRepository.hasShownNotificationCleanerExitPrompt()) {
            settingsRepository.saveNotificationCleanerExitPromptShown()
            return notificationCleaner
        }
        val suggestions = listOf(
            AppDestination.DeviceInfo,
            AppDestination.JunkClean,
            AppDestination.BatteryInfo,
            AppDestination.NetworkScan,
            AppDestination.NetworkUsage,
        )
        return suggestions.randomOrNull() ?: notificationCleaner
    }

    private fun scheduleAutoRatePrompt() {
        autoRatePromptJob?.cancel()
        if (!canShowAutoRatePrompt()) return
        if (externalBlockingPromptActive) return

        autoRatePromptJob =
            viewModelScope.launch(autoRateDispatcher) {
                delay(autoRatePromptDelayMillis)
                if (featureClicked) return@launch
                if (externalBlockingPromptActive) return@launch
                settingsRepository.saveLastAutoRatePromptAt(System.currentTimeMillis())
                changeState { it.copy(showAutoRateDialog = true) }
            }
    }

    private fun canShowAutoRatePrompt(): Boolean {
        val lastPromptAt = settingsRepository.readLastAutoRatePromptAt()
        if (lastPromptAt <= 0L) return true
        return System.currentTimeMillis() - lastPromptAt >= AUTO_RATE_PROMPT_COOLDOWN_MS
    }

    override fun onCleared() {
        autoRatePromptJob?.cancel()
        super.onCleared()
    }
}
