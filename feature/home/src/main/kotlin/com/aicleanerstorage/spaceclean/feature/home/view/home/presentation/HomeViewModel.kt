package com.aicleanerstorage.spaceclean.feature.home.view.home.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.device.BatteryInfo
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
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
) : MviViewModel<HomeUiState, HomeIntent, HomeEffect>(HomeUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnFeatureClicked -> onFeatureClicked()
            is HomeIntent.SetExternalBlockingPromptActive -> setExternalBlockingPromptActive(intent.active)
            HomeIntent.OnTabInteraction -> onTabInteraction()
            else -> {
        if (intent is HomeStateChanged) {
            updateState { HomeReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: HomeUiState) {
        synchronized(stateUpdateLock) { dispatch(HomeStateChanged(value)) }
    }

    private fun changeState(transform: (HomeUiState) -> HomeUiState) {
        synchronized(stateUpdateLock) { dispatch(HomeStateChanged(transform(currentState))) }
    }


    private var autoRatePromptJob: Job? = null
    private var entered = false
    private var featureClicked = false
    private var externalBlockingPromptActive = false

    init {
        refreshSummary()
        scheduleAutoRatePrompt()
        viewModelScope.launch {
            appLockRepository.lockedAppCountFlow.collect { count ->
                publishState(currentState.copy(
                    summary = currentState.summary.copy(lockedAppCount = count)
                ))
            }
        }
    }

    private fun handleIntent(action: HomeIntent) {
        when (action) {
            HomeIntent.RequestExitPrompt -> requestExitPrompt()
            HomeIntent.DismissExitPrompt -> dismissExitPrompt()
            HomeIntent.DismissAutoRateDialog -> dismissAutoRateDialog()
            HomeIntent.FeatureClicked -> onFeatureClicked()
            HomeIntent.TabInteracted -> onTabInteraction()
            HomeIntent.RefreshSummary -> refreshSummary()
            is HomeIntent.ExternalBlockingPromptChanged -> setExternalBlockingPromptActive(action.active)
            is HomeIntent.Entered -> if (!entered) { entered = true; selectTab(action.initialTab) }
            is HomeIntent.TabSelected -> { entered = true; onTabInteraction(); selectTab(action.index) }
            is HomeIntent.Navigate -> { onFeatureClicked(); emitEffect(HomeEffect.Navigate(action.destination)) }
            HomeIntent.ExitApp -> emitEffect(HomeEffect.ExitApp)
            HomeIntent.RateApp -> emitEffect(HomeEffect.RateApp)
            is HomeStateChanged -> Unit
            else -> Unit
        }
    }

    private fun selectTab(index: Int) {
        changeState { it.copy(selectedTab = index.coerceIn(0, 2)) }
    }

    private fun requestExitPrompt() {
        if (currentState.exitPromptRoute != null) return
        autoRatePromptJob?.cancel()
        publishState(currentState.copy(exitPromptRoute = nextExitPromptRoute()))
    }

    private fun dismissExitPrompt() {
        publishState(currentState.copy(exitPromptRoute = null))
    }

    private fun dismissAutoRateDialog() {
        publishState(currentState.copy(showAutoRateDialog = false))
    }

    private fun onFeatureClicked() {
        featureClicked = true
        autoRatePromptJob?.cancel()
        publishState(currentState.copy(showAutoRateDialog = false))
    }

    private fun setExternalBlockingPromptActive(active: Boolean) {
        if (externalBlockingPromptActive == active) return
        externalBlockingPromptActive = active
        publishState(currentState.copy(externalBlockingPromptActive = active))
        if (active) {
            autoRatePromptJob?.cancel()
            publishState(currentState.copy(showAutoRateDialog = false))
            return
        }
        scheduleAutoRatePrompt()
    }

    private fun onTabInteraction() {
        onFeatureClicked()
    }

    fun consumeExitPromptForNavigation(): String? {
        val route = currentState.exitPromptRoute ?: return null
        onFeatureClicked()
        publishState(currentState.copy(exitPromptRoute = null))
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
                        lockedAppCount = runCatching { appLockRepository.lockedAppCount() }.getOrDefault(0),
                        isLoading = false,
                    )
                }.getOrElse {
                    currentSummary.copy(isLoading = false)
                }
            publishState(this@HomeViewModel.currentState.copy(summary = loadedState))
        }
    }

    private fun nextExitPromptRoute(): String {
        val notificationCleanerRoute = AppDestination.NotificationCleaner.route
        if (!settingsRepository.hasShownNotificationCleanerExitPrompt()) {
            settingsRepository.saveNotificationCleanerExitPromptShown()
            return notificationCleanerRoute
        }
        val suggestions = listOf(
            AppDestination.DeviceInfo.route,
            AppDestination.JunkClean.route,
            AppDestination.BatteryInfo.route,
            AppDestination.NetworkScan.route,
            AppDestination.NetworkUsage.route,
        )
        return suggestions.randomOrNull() ?: notificationCleanerRoute
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
                publishState(currentState.copy(showAutoRateDialog = true))
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
