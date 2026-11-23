package com.aicleanerstorage.spaceclean.app.view.home.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val AUTO_RATE_PROMPT_DELAY_MS = 15_000L
private const val AUTO_RATE_PROMPT_COOLDOWN_MS = 24L * 60 * 60 * 1000

class HomeViewModel(
    private val repository: DeviceInfoRepository,
    private val appLockRepository: AppLockRepository,
    private val settingsRepository: AppSettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val autoRateDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val autoRatePromptDelayMillis: Long = AUTO_RATE_PROMPT_DELAY_MS,
) : MviViewModel<HomeUiState, HomeAction, HomeEvent>(HomeUiState()) {
    private var autoRatePromptJob: Job? = null
    private var summaryJob: Job? = null
    private var entered = false
    private var featureClicked = false
    private var externalBlockingPromptActive = false

    init {
        refreshSummary()
        scheduleAutoRatePrompt()
        viewModelScope.launch {
            appLockRepository.observeLockedAppCount().collect { count ->
                setState { it.copy(summary = it.summary.copy(lockedAppCount = count)) }
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
            is HomeAction.Entered ->
                if (!entered) {
                    entered = true
                    selectTab(action.initialTab)
                }
            is HomeAction.TabSelected -> {
                entered = true
                onTabInteraction()
                selectTab(action.index)
            }
            is HomeAction.NavigationRequested -> {
                onFeatureClicked()
                sendEvent(HomeEvent.Navigate(action.destination))
            }
            HomeAction.ExitConfirmed -> sendEvent(HomeEvent.ExitApp)
            HomeAction.RateAppClicked -> sendEvent(HomeEvent.RateApp)
            HomeAction.ExitPromptViewClicked -> {
                consumeExitPromptForNavigation()?.let { sendEvent(HomeEvent.Navigate(it)) }
            }
        }
    }

    private fun selectTab(index: Int) {
        setState { it.copy(selectedTab = index.coerceIn(0, 2)) }
    }

    private fun requestExitPrompt() {
        if (currentState.exitPromptDestination != null) return
        autoRatePromptJob?.cancel()
        val destination = nextExitPromptDestination()
        setState { it.copy(exitPromptDestination = destination) }
    }

    private fun dismissExitPrompt() {
        setState { it.copy(exitPromptDestination = null) }
    }

    private fun dismissAutoRateDialog() {
        setState { it.copy(showAutoRateDialog = false) }
    }

    private fun onFeatureClicked() {
        featureClicked = true
        autoRatePromptJob?.cancel()
        setState { it.copy(showAutoRateDialog = false) }
    }

    private fun setExternalBlockingPromptActive(active: Boolean) {
        if (externalBlockingPromptActive == active) return
        externalBlockingPromptActive = active
        setState { it.copy(externalBlockingPromptActive = active) }
        if (active) {
            autoRatePromptJob?.cancel()
            setState { it.copy(showAutoRateDialog = false) }
            return
        }
        scheduleAutoRatePrompt()
    }

    private fun onTabInteraction() {
        onFeatureClicked()
    }

    private fun consumeExitPromptForNavigation(): AppDestination? {
        val route = currentState.exitPromptDestination ?: return null
        onFeatureClicked()
        setState { it.copy(exitPromptDestination = null) }
        return route
    }

    private fun refreshSummary() {
        if (summaryJob?.isActive == true) return
        summaryJob =
            viewModelScope.launch(ioDispatcher) {
                val currentSummary = currentState.summary
                val loadedState =
                    runCatching {
                        val hardware = repository.hardwareInfo()
                        HomeSummaryUiState(
                            storageInfo = repository.internalStorageInfo(),
                            batteryInfo = repository.batteryInfo(),
                            deviceModel = hardware.model,
                            androidVersion = hardware.androidVersion,
                            lockedAppCount = runCatching { appLockRepository.currentLockedAppCount() }.getOrDefault(0),
                            isLoading = false,
                        )
                    }.getOrElse {
                        currentSummary.copy(isLoading = false)
                    }
                setState { it.copy(summary = loadedState) }
            }
    }

    private fun nextExitPromptDestination(): AppDestination {
        val notificationCleaner = AppDestination.NotificationCleaner
        if (!settingsRepository.hasShownNotificationCleanerExitPrompt()) {
            settingsRepository.saveNotificationCleanerExitPromptShown()
            return notificationCleaner
        }
        val suggestions =
            listOf(
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
                setState { it.copy(showAutoRateDialog = true) }
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
