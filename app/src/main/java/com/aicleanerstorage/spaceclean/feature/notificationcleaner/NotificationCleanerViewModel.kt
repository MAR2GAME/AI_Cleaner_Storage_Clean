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
    Cleaning,
    Complete,
}

data class NotificationCleanerUiState(
    val hasAccess: Boolean = false,
    val enabled: Boolean = false,
    val blockedCount: Int = 0,
    val blockedCountsByPackage: Map<String, Int> = emptyMap(),
    val blockedNotifications: List<BlockedNotification> = emptyList(),
    val apps: List<BlockableNotificationApp> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val appsLoaded: Boolean = false,
    val isAppsLoading: Boolean = false,
    val page: NotificationCleanerPage = NotificationCleanerPage.Onboarding,
    val isLoading: Boolean = true,
    val scanInProgress: Boolean = false,
    val isInitialized: Boolean = false,
    val errorMessage: String? = null,
    val completionAdInFlight: Boolean = false,
    val cleanedCount: Int = 0,
    val cleaningAnimationComplete: Boolean = false,
    val completionResultVisible: Boolean = false,
    val showStopDialog: Boolean = false,
    val stopDialogPage: NotificationCleanerPage? = null,
)

sealed interface NotificationCleanerAction {
    data object Back : NotificationCleanerAction
    data object RequestStop : NotificationCleanerAction
    data object QuitStoppedOperation : NotificationCleanerAction
    data object ResumeStoppedOperation : NotificationCleanerAction
    data object Refresh : NotificationCleanerAction
    data object ShowSettings : NotificationCleanerAction
    data object LeaveSettings : NotificationCleanerAction
    data object EnableBlocking : NotificationCleanerAction
    data object DisableBlocking : NotificationCleanerAction
    data object TidyUp : NotificationCleanerAction
    data object FinishScanning : NotificationCleanerAction
    data object FinishCleaning : NotificationCleanerAction
    data object FinishCompleteAnimation : NotificationCleanerAction
    data object ShowStatusAfterCompletionAd : NotificationCleanerAction
    data object CompletionAdStarted : NotificationCleanerAction
    data object CompletionAdFinished : NotificationCleanerAction
    data class ShowStatus(val scanGeneration: Long) : NotificationCleanerAction
    data class TogglePackage(val packageName: String) : NotificationCleanerAction
}

internal data class NotificationCleanerOperationEvent(
    val event: FeatureOperationEvent,
    val scanGeneration: Long? = null,
)

class NotificationCleanerViewModel(
    application: Application,
    private val repository: NotificationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val settingsGateway: NotificationSettingsGateway = AndroidNotificationSettingsGateway(),
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(NotificationCleanerUiState())
    private val operationEventsChannel = Channel<NotificationCleanerOperationEvent>(Channel.BUFFERED)
    private var scanJob: Job? = null
    private var activeScanGeneration = 0L
    private var pendingScanCompletionGeneration: Long? = null
    private var deferredScanCompletionGeneration: Long? = null
    private var cleaningCompletionPending = false
    private var postCompletionPage: NotificationCleanerPage? = null

    val uiState: StateFlow<NotificationCleanerUiState> = _uiState.asStateFlow()
    internal val operationEvents: Flow<NotificationCleanerOperationEvent> = operationEventsChannel.receiveAsFlow()

    init {
        refreshState()
    }

    fun onAction(action: NotificationCleanerAction) {
        when (action) {
            NotificationCleanerAction.Back -> Unit
            NotificationCleanerAction.RequestStop -> requestStopDialog()
            NotificationCleanerAction.QuitStoppedOperation -> confirmStoppedOperationExit()
            NotificationCleanerAction.ResumeStoppedOperation -> resumeStoppedOperation()
            NotificationCleanerAction.Refresh -> refreshState()
            NotificationCleanerAction.ShowSettings -> showSettings()
            NotificationCleanerAction.LeaveSettings -> leaveSettings()
            NotificationCleanerAction.EnableBlocking -> setBlockingEnabled(true)
            NotificationCleanerAction.DisableBlocking -> setBlockingEnabled(false)
            NotificationCleanerAction.TidyUp -> tidyUp()
            NotificationCleanerAction.FinishScanning -> finishScanning()
            NotificationCleanerAction.FinishCleaning -> finishCleaning()
            NotificationCleanerAction.FinishCompleteAnimation -> finishCompleteAnimation()
            NotificationCleanerAction.ShowStatusAfterCompletionAd -> showStatusAfterCompletionAd()
            NotificationCleanerAction.CompletionAdStarted ->
                _uiState.update { it.copy(completionAdInFlight = true) }
            NotificationCleanerAction.CompletionAdFinished ->
                _uiState.update { it.copy(completionAdInFlight = false) }
            is NotificationCleanerAction.ShowStatus -> showStatus(action.scanGeneration)
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
        loadNotificationAppsIfNeeded()
    }

    fun leaveSettings() {
        _uiState.update { it.copy(page = NotificationCleanerPage.Status) }
    }

    fun finishScanning() {
        finishScanIfReady(activeScanGeneration)
    }

    fun showStatus(scanGeneration: Long) {
        if (
            scanGeneration != activeScanGeneration ||
            pendingScanCompletionGeneration != scanGeneration
        ) {
            return
        }
        val state = _uiState.value
        if (
            state.showStopDialog &&
            state.stopDialogPage == NotificationCleanerPage.Scanning
        ) {
            deferredScanCompletionGeneration = scanGeneration
            return
        }
        completeScanStatus(scanGeneration)
    }

    private fun completeScanStatus(scanGeneration: Long) {
        if (
            scanGeneration != activeScanGeneration ||
            pendingScanCompletionGeneration != scanGeneration
        ) {
            return
        }
        pendingScanCompletionGeneration = null
        deferredScanCompletionGeneration = null
        _uiState.update {
            if (it.page == NotificationCleanerPage.Scanning) {
                it.copy(
                    page = NotificationCleanerPage.Status,
                    scanInProgress = false,
                    showStopDialog = false,
                    stopDialogPage = null,
                )
            } else {
                it
            }
        }
    }

    private fun requestStopDialog() {
        val stoppedPage = _uiState.value.page
        if (
            stoppedPage != NotificationCleanerPage.Scanning &&
            stoppedPage != NotificationCleanerPage.Cleaning
        ) {
            return
        }
        _uiState.update {
            if (it.page == stoppedPage) {
                it.copy(showStopDialog = true, stopDialogPage = stoppedPage)
            } else {
                it
            }
        }
        if (
            stoppedPage == NotificationCleanerPage.Scanning &&
            pendingScanCompletionGeneration != activeScanGeneration
        ) {
            cancelActiveScan()
        }
    }

    private fun resumeStoppedOperation() {
        val stoppedPage = _uiState.value.stopDialogPage
        val scanCompletionPending = pendingScanCompletionGeneration == activeScanGeneration
        _uiState.update { it.copy(showStopDialog = false, stopDialogPage = null) }
        if (stoppedPage != NotificationCleanerPage.Scanning) return

        val deferredGeneration = deferredScanCompletionGeneration
        when {
            deferredGeneration == activeScanGeneration -> completeScanStatus(deferredGeneration)
            scanCompletionPending -> Unit
            else -> startAccessScan()
        }
    }

    private fun confirmStoppedOperationExit() {
        if (_uiState.value.stopDialogPage == NotificationCleanerPage.Scanning) {
            cancelActiveScan()
        }
        _uiState.update { it.copy(showStopDialog = false, stopDialogPage = null) }
    }

    private fun cancelActiveScan() {
        activeScanGeneration += 1L
        pendingScanCompletionGeneration = null
        deferredScanCompletionGeneration = null
        scanJob?.cancel()
        scanJob = null
        _uiState.update {
            if (it.page == NotificationCleanerPage.Scanning) {
                it.copy(scanInProgress = false, isLoading = false)
            } else {
                it
            }
        }
    }

    fun notificationListenerSettingsIntent(): Intent = settingsGateway.notificationListenerSettingsIntent()

    fun appDetailsSettingsIntent(packageName: String): Intent = settingsGateway.appDetailsSettingsIntent(packageName)

    private fun handleAccessRefresh(hasAccess: Boolean) {
        val state = _uiState.value
        if (!hasAccess) {
            activeScanGeneration += 1L
            scanJob?.cancel()
            scanJob = null
            pendingScanCompletionGeneration = null
            deferredScanCompletionGeneration = null
            cleaningCompletionPending = false
            postCompletionPage = null
            _uiState.update {
                it.copy(
                    hasAccess = false,
                    enabled = false,
                    blockedCount = 0,
                    blockedCountsByPackage = emptyMap(),
                    blockedNotifications = emptyList(),
                    apps = emptyList(),
                    selectedPackages = emptySet(),
                    appsLoaded = false,
                    isAppsLoading = false,
                    page = NotificationCleanerPage.Onboarding,
                    isLoading = false,
                    scanInProgress = false,
                    isInitialized = true,
                    errorMessage = null,
                    completionAdInFlight = false,
                    cleanedCount = 0,
                    cleaningAnimationComplete = false,
                    completionResultVisible = false,
                    showStopDialog = false,
                    stopDialogPage = null,
                )
            }
            return
        }

        if (state.page == NotificationCleanerPage.Scanning && state.scanInProgress) {
            return
        }

        val shouldScan =
            !state.isInitialized ||
                !state.hasAccess

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
                        blockedNotifications = snapshot.blockedNotifications,
                        selectedPackages = snapshot.selectedPackages,
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
        activeScanGeneration += 1L
        val scanGeneration = activeScanGeneration
        pendingScanCompletionGeneration = null
        deferredScanCompletionGeneration = null
        _uiState.update {
            it.copy(
                hasAccess = true,
                page = NotificationCleanerPage.Scanning,
                isLoading = true,
                scanInProgress = true,
                isInitialized = true,
                errorMessage = null,
                showStopDialog = false,
                stopDialogPage = null,
            )
        }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                val startedAtMillis = System.currentTimeMillis()
                try {
                    val snapshot = repository.notificationSnapshot()
                    val remainingMillis = MIN_SCAN_DURATION_MILLIS - (System.currentTimeMillis() - startedAtMillis)
                    if (remainingMillis > 0L) delay(remainingMillis)
                    if (scanGeneration != activeScanGeneration) return@launch
                    _uiState.update { state ->
                        if (!snapshot.hasAccess) {
                            state.copy(
                                hasAccess = false,
                                enabled = false,
                                blockedCount = 0,
                                blockedCountsByPackage = emptyMap(),
                                blockedNotifications = emptyList(),
                                apps = emptyList(),
                                selectedPackages = emptySet(),
                                appsLoaded = false,
                                isAppsLoading = false,
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
                                blockedNotifications = snapshot.blockedNotifications,
                                apps = snapshot.apps,
                                selectedPackages = snapshot.selectedPackages,
                                appsLoaded = snapshot.apps.isNotEmpty(),
                                isAppsLoading = false,
                                page = NotificationCleanerPage.Scanning,
                                isLoading = false,
                                scanInProgress = false,
                                isInitialized = true,
                                errorMessage = null,
                            )
                        }
                    }
                    if (scanGeneration != activeScanGeneration) return@launch
                    scanJob = null
                    finishScanIfReady(scanGeneration)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    if (scanGeneration != activeScanGeneration) return@launch
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
            blockedNotifications = repository.blockedNotifications(),
            selectedPackages = repository.selectedNotificationPackages(),
        )

    private fun finishScanIfReady(scanGeneration: Long) {
        val state = _uiState.value
        if (
            scanGeneration != activeScanGeneration ||
            state.page != NotificationCleanerPage.Scanning ||
            state.showStopDialog ||
            state.isLoading ||
            state.scanInProgress ||
            pendingScanCompletionGeneration != null
        ) {
            return
        }
        pendingScanCompletionGeneration = scanGeneration
        trackOperationEvent(
            FeatureOperationEvent.ScanFinished(
                FeatureKey.NOTIFICATION_CLEANER,
                state.blockedCount > 0,
            ),
            scanGeneration = scanGeneration,
        )
    }

    fun tidyUp() {
        val state = _uiState.value
        if (state.page != NotificationCleanerPage.Status || state.blockedCount <= 0 || state.completionAdInFlight) return
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching { repository.clearBlockedNotifications() }
                .onSuccess { cleanedCount ->
                    if (cleanedCount <= 0) {
                        refreshState()
                        return@onSuccess
                    }
                    postCompletionPage = null
                    cleaningCompletionPending = false
                    _uiState.update {
                        it.copy(
                            page = NotificationCleanerPage.Cleaning,
                            blockedCount = 0,
                            blockedCountsByPackage = emptyMap(),
                            blockedNotifications = emptyList(),
                            cleanedCount = cleanedCount,
                            cleaningAnimationComplete = false,
                            completionResultVisible = false,
                            errorMessage = null,
                        )
                    }
                    trackOperationEvent(
                        FeatureOperationEvent.OperationStarted(
                            FeatureKey.NOTIFICATION_CLEANER,
                            OperationAction.CLEAN,
                        ),
                    )
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: appString(R.string.notification_status_load_failed))
                    }
                }
        }
    }

    fun finishCleaning() {
        if (_uiState.value.page != NotificationCleanerPage.Cleaning || cleaningCompletionPending) return
        cleaningCompletionPending = true
        postCompletionPage = NotificationCleanerPage.Complete
        _uiState.update {
            it.copy(
                page = NotificationCleanerPage.Complete,
                cleaningAnimationComplete = false,
                completionResultVisible = false,
            )
        }
    }

    fun finishCompleteAnimation() {
        if (_uiState.value.page != NotificationCleanerPage.Complete || !cleaningCompletionPending) return
        cleaningCompletionPending = false
        trackOperationEvent(
            FeatureOperationEvent.OperationFinished(
                FeatureKey.NOTIFICATION_CLEANER,
                OperationAction.CLEAN,
                success = true,
            ),
        )
    }

    fun showStatusAfterCompletionAd() {
        val targetPage = postCompletionPage ?: NotificationCleanerPage.Status
        cleaningCompletionPending = false
        postCompletionPage = null
        _uiState.update {
            when (targetPage) {
                NotificationCleanerPage.Complete ->
                    it.copy(
                        page = NotificationCleanerPage.Complete,
                        cleaningAnimationComplete = false,
                        completionResultVisible = true,
                    )
                else ->
                    it.copy(
                        page = NotificationCleanerPage.Status,
                        cleaningAnimationComplete = false,
                        completionResultVisible = false,
                    )
            }
        }
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

    private fun loadNotificationAppsIfNeeded() {
        val current = _uiState.value
        if (current.appsLoaded || current.isAppsLoading) return
        _uiState.update { it.copy(isAppsLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                NotificationAppsSnapshot(
                    apps = repository.notificationApps(),
                    selectedPackages = repository.selectedNotificationPackages(),
                )
            }.onSuccess { snapshot ->
                _uiState.update {
                    it.copy(
                        apps = snapshot.apps,
                        selectedPackages = snapshot.selectedPackages,
                        appsLoaded = true,
                        isAppsLoading = false,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAppsLoading = false,
                        errorMessage = error.message ?: appString(R.string.notification_status_load_failed),
                    )
                }
            }
        }
    }

    private fun trackOperationEvent(
        event: FeatureOperationEvent,
        scanGeneration: Long? = null,
    ) {
        operationEventsChannel.trySend(
            NotificationCleanerOperationEvent(
                event = event,
                scanGeneration = scanGeneration,
            ),
        )
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
    val blockedNotifications: List<BlockedNotification>,
    val selectedPackages: Set<String>,
)

private data class NotificationAppsSnapshot(
    val apps: List<BlockableNotificationApp>,
    val selectedPackages: Set<String>,
)

