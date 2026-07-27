package com.aicleanerstorage.spaceclean.feature.notificationcleaner

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.coroutines.runSuspendCatching
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.BlockableNotificationApp
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationRepository
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.AndroidNotificationSettingsGateway
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationSettingsGateway
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationCleanerPage {
    Onboarding,
    Scanning,
    Status,
    Settings,
}

data class NotificationCleanerUiState(
    val hasAccess: Boolean = false,
    val enabled: Boolean = false,
    val blockedCount: Int = 0,
    val blockedCountsByPackage: Map<String, Int> = emptyMap(),
    val apps: List<BlockableNotificationApp> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val page: NotificationCleanerPage = NotificationCleanerPage.Onboarding,
    val isLoading: Boolean = true,
    val scanInProgress: Boolean = false,
    val isInitialized: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface NotificationCleanerAction {
    data object Back : NotificationCleanerAction
    data object Refresh : NotificationCleanerAction
    data object ShowSettings : NotificationCleanerAction
    data object LeaveSettings : NotificationCleanerAction
    data object EnableBlocking : NotificationCleanerAction
    data object DisableBlocking : NotificationCleanerAction
    data object FinishScanning : NotificationCleanerAction
    data object ShowStatus : NotificationCleanerAction
    data class TogglePackage(val packageName: String) : NotificationCleanerAction
}

class NotificationCleanerViewModel(
    application: Application,
    private val repository: NotificationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val settingsGateway: NotificationSettingsGateway = AndroidNotificationSettingsGateway(),
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(NotificationCleanerUiState())
    private val operationEventsChannel = Channel<FeatureOperationEvent>(Channel.BUFFERED)
    private var scanJob: Job? = null
    private var scanningCompletionPending = false

    val uiState: StateFlow<NotificationCleanerUiState> = _uiState.asStateFlow()
    val operationEvents: Flow<FeatureOperationEvent> = operationEventsChannel.receiveAsFlow()

    init {
        refreshState()
    }

    fun onAction(action: NotificationCleanerAction) {
        when (action) {
            NotificationCleanerAction.Back -> Unit
            NotificationCleanerAction.Refresh -> refreshState()
            NotificationCleanerAction.ShowSettings -> showSettings()
            NotificationCleanerAction.LeaveSettings -> leaveSettings()
            NotificationCleanerAction.EnableBlocking -> setBlockingEnabled(true)
            NotificationCleanerAction.DisableBlocking -> setBlockingEnabled(false)
            NotificationCleanerAction.FinishScanning -> finishScanning()
            NotificationCleanerAction.ShowStatus -> showStatus()
            is NotificationCleanerAction.TogglePackage -> togglePackage(action.packageName)
        }
    }

    fun refreshState() {
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching { repository.hasNotificationListenerAccess() }
                .onSuccess { hasAccess ->
                    handleAccessRefresh(hasAccess)
                }.onFailure { error ->
                    failRefresh(error)
                }
        }
    }

    fun setBlockingEnabled(checked: Boolean): Boolean {
        val hasAccess = repository.hasNotificationListenerAccess()
        if (checked && !hasAccess) {
            _uiState.update {
                it.copy(hasAccess = false, enabled = false, isLoading = false, scanInProgress = false)
            }
            return true
        }

        repository.setNotificationBlockingEnabled(checked)
        refreshState()
        return false
    }

    fun togglePackage(packageName: String) {
        if (!_uiState.value.enabled) return
        val selected = packageName !in _uiState.value.selectedPackages
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                repository.setNotificationPackageSelected(packageName, selected)
                repository.selectedNotificationPackages()
            }.onSuccess { selectedPackages ->
                _uiState.update { it.copy(selectedPackages = selectedPackages, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: appString(R.string.notification_app_selection_update_failed))
                }
            }
        }
    }

    fun showSettings() {
        _uiState.update { it.copy(page = NotificationCleanerPage.Settings) }
    }

    fun leaveSettings() {
        _uiState.update { it.copy(page = NotificationCleanerPage.Status) }
    }

    fun finishScanning() {
        finishScanIfReady()
    }

    fun showStatus() {
        scanningCompletionPending = false
        _uiState.update {
            if (it.page == NotificationCleanerPage.Scanning) {
                it.copy(page = NotificationCleanerPage.Status, scanInProgress = false)
            } else {
                it
            }
        }
    }

    fun cancelScanning() {
        scanJob?.cancel()
        scanJob = null
        scanningCompletionPending = false
        _uiState.update {
            if (it.page == NotificationCleanerPage.Scanning) {
                it.copy(page = NotificationCleanerPage.Status, scanInProgress = false, isLoading = false)
            } else {
                it
            }
        }
    }

    fun restartScanning() {
        startAccessScan()
    }

    fun notificationListenerSettingsIntent(): Intent = settingsGateway.notificationListenerSettingsIntent()

    fun appDetailsSettingsIntent(packageName: String): Intent = settingsGateway.appDetailsSettingsIntent(packageName)

    private fun handleAccessRefresh(hasAccess: Boolean) {
        val state = _uiState.value
        if (!hasAccess) {
            scanJob?.cancel()
            scanJob = null
            scanningCompletionPending = false
            _uiState.update {
                it.copy(
                    hasAccess = false,
                    enabled = false,
                    blockedCount = 0,
                    blockedCountsByPackage = emptyMap(),
                    apps = emptyList(),
                    selectedPackages = emptySet(),
                    page = NotificationCleanerPage.Onboarding,
                    isLoading = false,
                    scanInProgress = false,
                    isInitialized = true,
                    errorMessage = null,
                )
            }
            return
        }

        if (state.page == NotificationCleanerPage.Scanning && state.scanInProgress) {
            return
        }

        val shouldScan =
            !state.isInitialized ||
                !state.hasAccess ||
                state.apps.isEmpty()

        if (shouldScan) {
            startAccessScan()
        } else {
            refreshLightState()
        }
    }

    private fun refreshLightState() {
        runCatching { readLightSnapshot() }
            .onSuccess { snapshot ->
                _uiState.update { state ->
                    state.copy(
                        hasAccess = true,
                        enabled = snapshot.enabled,
                        blockedCount = snapshot.blockedCount,
                        blockedCountsByPackage = snapshot.blockedCountsByPackage,
                        isLoading = false,
                        scanInProgress = false,
                        isInitialized = true,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                failRefresh(error)
            }
    }

    private fun startAccessScan() {
        scanJob?.cancel()
        scanJob = null
        scanningCompletionPending = false
        _uiState.update {
            it.copy(
                hasAccess = true,
                page = NotificationCleanerPage.Scanning,
                isLoading = true,
                scanInProgress = true,
                isInitialized = true,
                errorMessage = null,
            )
        }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                val startedAtMillis = System.currentTimeMillis()
                try {
                    val snapshot = repository.notificationSnapshot()
                    val remainingMillis = MIN_SCAN_DURATION_MILLIS - (System.currentTimeMillis() - startedAtMillis)
                    if (remainingMillis > 0L) delay(remainingMillis)
                    _uiState.update { state ->
                        if (!snapshot.hasAccess) {
                            state.copy(
                                hasAccess = false,
                                enabled = false,
                                blockedCount = 0,
                                blockedCountsByPackage = emptyMap(),
                                apps = emptyList(),
                                selectedPackages = emptySet(),
                                page = NotificationCleanerPage.Onboarding,
                                isLoading = false,
                                scanInProgress = false,
                                isInitialized = true,
                                errorMessage = null,
                            )
                        } else {
                            state.copy(
                                hasAccess = true,
                                enabled = snapshot.enabled,
                                blockedCount = snapshot.blockedCount,
                                blockedCountsByPackage = snapshot.blockedCountsByPackage,
                                apps = snapshot.apps,
                                selectedPackages = snapshot.selectedPackages,
                                page = NotificationCleanerPage.Scanning,
                                isLoading = false,
                                scanInProgress = false,
                                isInitialized = true,
                                errorMessage = null,
                            )
                        }
                    }
                    scanJob = null
                    finishScanIfReady()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    scanJob = null
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scanInProgress = false,
                            isInitialized = true,
                            errorMessage = error.message ?: appString(R.string.notification_status_load_failed),
                        )
                    }
                }
            }
    }

    private fun readLightSnapshot(): NotificationCleanerLightSnapshot =
        NotificationCleanerLightSnapshot(
            enabled = repository.isNotificationBlockingEnabled(),
            blockedCount = repository.blockedNotificationCount(),
            blockedCountsByPackage = repository.blockedNotificationCountsByPackage(),
        )

    private fun finishScanIfReady() {
        val state = _uiState.value
        if (
            state.page != NotificationCleanerPage.Scanning ||
            state.isLoading ||
            state.scanInProgress ||
            scanningCompletionPending
        ) {
            return
        }
        scanningCompletionPending = true
        trackOperationEvent(
            FeatureOperationEvent.OperationFinished(
                FeatureKey.NOTIFICATION_CLEANER,
                OperationAction.CLEAN,
                success = true,
            ),
        )
    }

    private fun failRefresh(error: Throwable) {
        _uiState.update {
            it.copy(
                isLoading = false,
                scanInProgress = false,
                isInitialized = true,
                errorMessage = error.message ?: appString(R.string.notification_status_load_failed),
            )
        }
    }

    private fun appString(resId: Int): String = getApplication<Application>().getString(resId)

    private fun trackOperationEvent(event: FeatureOperationEvent) {
        operationEventsChannel.trySend(event)
    }

    override fun onCleared() {
        scanJob?.cancel()
        super.onCleared()
    }

    private companion object {
        private const val MIN_SCAN_DURATION_MILLIS = 1_800L
    }
}

private data class NotificationCleanerLightSnapshot(
    val enabled: Boolean,
    val blockedCount: Int,
    val blockedCountsByPackage: Map<String, Int>,
)

