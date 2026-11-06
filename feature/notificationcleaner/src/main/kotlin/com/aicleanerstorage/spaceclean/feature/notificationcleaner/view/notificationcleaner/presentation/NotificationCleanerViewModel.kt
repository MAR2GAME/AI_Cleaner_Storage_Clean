package com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation

import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.BlockableNotificationApp
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.BlockedNotification
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.NotificationRepository
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.coroutines.runSuspendCatching
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationCleanerViewModel(
    private val message: (Int) -> String,
    private val repository: NotificationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NotificationCleanerUiState, NotificationCleanerIntent, NotificationCleanerEffect>(NotificationCleanerUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: NotificationCleanerIntent) {
        when (intent) {
            is NotificationCleanerIntent.Navigate -> emitEffect(NotificationCleanerEffect.Navigate(intent.destination))
            NotificationCleanerIntent.RefreshState -> refreshState()
            else -> {
        if (intent is NotificationCleanerStateChanged) {
            updateState { NotificationCleanerReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: NotificationCleanerUiState) {
        synchronized(stateUpdateLock) { dispatch(NotificationCleanerStateChanged(value)) }
    }

    private fun changeState(transform: (NotificationCleanerUiState) -> NotificationCleanerUiState) {
        synchronized(stateUpdateLock) { dispatch(NotificationCleanerStateChanged(transform(currentState))) }
    }


    private var scanJob: Job? = null
    private var activeScanGeneration = 0L
    private var pendingScanCompletionGeneration: Long? = null
    private var deferredScanCompletionGeneration: Long? = null
    private var cleaningCompletionPending = false
    private var postCompletionPage: NotificationCleanerPage? = null


    init {
        refreshState()
    }

    private fun handleIntent(action: NotificationCleanerIntent) {
        when (action) {
            NotificationCleanerIntent.Back -> {
                if (!currentState.completionAdInFlight) when (currentState.page) {
                    NotificationCleanerPage.Settings -> leaveSettings()
                    NotificationCleanerPage.Scanning, NotificationCleanerPage.Cleaning -> requestStopDialog()
                    else -> emitEffect(NotificationCleanerEffect.Exit)
                }
            }
            NotificationCleanerIntent.RequestStop -> requestStopDialog()
            NotificationCleanerIntent.QuitStoppedOperation -> { confirmStoppedOperationExit(); emitEffect(NotificationCleanerEffect.Exit) }
            NotificationCleanerIntent.ResumeStoppedOperation -> resumeStoppedOperation()
            NotificationCleanerIntent.Refresh -> refreshState()
            NotificationCleanerIntent.ShowSettings -> showSettings()
            NotificationCleanerIntent.LeaveSettings -> leaveSettings()
            NotificationCleanerIntent.EnableBlocking -> setBlockingEnabled(true)
            NotificationCleanerIntent.DisableBlocking -> setBlockingEnabled(false)
            NotificationCleanerIntent.TidyUp -> tidyUp()
            NotificationCleanerIntent.FinishScanning -> finishScanning()
            NotificationCleanerIntent.FinishCleaning -> finishCleaning()
            NotificationCleanerIntent.FinishCompleteAnimation -> finishCompleteAnimation()
            NotificationCleanerIntent.ShowStatusAfterCompletionAd -> showStatusAfterCompletionAd()
            NotificationCleanerIntent.CompletionAdStarted ->
                changeState { it.copy(completionAdInFlight = true) }
            NotificationCleanerIntent.CompletionAdFinished ->
                changeState { it.copy(completionAdInFlight = false) }
            is NotificationCleanerIntent.ShowStatus -> showStatus(action.scanGeneration)
            is NotificationCleanerIntent.TogglePackage -> togglePackage(action.packageName)
            is NotificationCleanerStateChanged -> Unit
            else -> Unit
        }
    }

    private fun refreshState() {
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching { repository.hasNotificationListenerAccess() }
                .onSuccess { hasAccess ->
                    handleAccessRefresh(hasAccess)
                }.onFailure { error ->
                    failRefresh(error)
                }
        }
    }

    private fun setBlockingEnabled(checked: Boolean): Boolean {
        val hasAccess = repository.hasNotificationListenerAccess()
        if (checked && !hasAccess) {
            changeState {
                it.copy(hasAccess = false, enabled = false, isLoading = false, scanInProgress = false)
            }
            return true
        }

        repository.setNotificationBlockingEnabled(checked)
        refreshState()
        return false
    }

    private fun togglePackage(packageName: String) {
        if (!currentState.enabled) return
        val selected = packageName !in currentState.selectedPackages
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                repository.setNotificationPackageSelected(packageName, selected)
                repository.selectedNotificationPackages()
            }.onSuccess { selectedPackages ->
                changeState { it.copy(selectedPackages = selectedPackages, errorMessage = null) }
            }.onFailure { error ->
                changeState {
                    it.copy(errorMessage = error.message ?: appString(R.string.notification_app_selection_update_failed))
                }
            }
        }
    }

    private fun showSettings() {
        changeState { it.copy(page = NotificationCleanerPage.Settings) }
        loadNotificationAppsIfNeeded()
    }

    private fun leaveSettings() {
        changeState { it.copy(page = NotificationCleanerPage.Status) }
    }

    private fun finishScanning() {
        finishScanIfReady(activeScanGeneration)
    }

    private fun showStatus(scanGeneration: Long) {
        if (
            scanGeneration != activeScanGeneration ||
            pendingScanCompletionGeneration != scanGeneration
        ) {
            return
        }
        val state = currentState
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
        changeState {
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
        val stoppedPage = currentState.page
        if (
            stoppedPage != NotificationCleanerPage.Scanning &&
            stoppedPage != NotificationCleanerPage.Cleaning
        ) {
            return
        }
        changeState {
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
        val stoppedPage = currentState.stopDialogPage
        val scanCompletionPending = pendingScanCompletionGeneration == activeScanGeneration
        changeState { it.copy(showStopDialog = false, stopDialogPage = null) }
        if (stoppedPage != NotificationCleanerPage.Scanning) return

        val deferredGeneration = deferredScanCompletionGeneration
        when {
            deferredGeneration == activeScanGeneration -> completeScanStatus(deferredGeneration)
            scanCompletionPending -> Unit
            else -> startAccessScan()
        }
    }

    private fun confirmStoppedOperationExit() {
        if (currentState.stopDialogPage == NotificationCleanerPage.Scanning) {
            cancelActiveScan()
        }
        changeState { it.copy(showStopDialog = false, stopDialogPage = null) }
    }

    private fun cancelActiveScan() {
        activeScanGeneration += 1L
        pendingScanCompletionGeneration = null
        deferredScanCompletionGeneration = null
        scanJob?.cancel()
        scanJob = null
        changeState {
            if (it.page == NotificationCleanerPage.Scanning) {
                it.copy(scanInProgress = false, isLoading = false)
            } else {
                it
            }
        }
    }

    private fun handleAccessRefresh(hasAccess: Boolean) {
        val state = currentState
        if (!hasAccess) {
            activeScanGeneration += 1L
            scanJob?.cancel()
            scanJob = null
            pendingScanCompletionGeneration = null
            deferredScanCompletionGeneration = null
            cleaningCompletionPending = false
            postCompletionPage = null
            changeState {
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
                changeState { state ->
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
        changeState {
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
                    changeState { state ->
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
                    changeState {
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
        val state = currentState
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

    private fun tidyUp() {
        val state = currentState
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
                    changeState {
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
                    changeState {
                        it.copy(errorMessage = error.message ?: appString(R.string.notification_status_load_failed))
                    }
                }
        }
    }

    private fun finishCleaning() {
        if (currentState.page != NotificationCleanerPage.Cleaning || cleaningCompletionPending) return
        cleaningCompletionPending = true
        postCompletionPage = NotificationCleanerPage.Complete
        changeState {
            it.copy(
                page = NotificationCleanerPage.Complete,
                cleaningAnimationComplete = false,
                completionResultVisible = false,
            )
        }
    }

    private fun finishCompleteAnimation() {
        if (currentState.page != NotificationCleanerPage.Complete || !cleaningCompletionPending) return
        cleaningCompletionPending = false
        trackOperationEvent(
            FeatureOperationEvent.OperationFinished(
                FeatureKey.NOTIFICATION_CLEANER,
                OperationAction.CLEAN,
                success = true,
            ),
        )
    }

    private fun showStatusAfterCompletionAd() {
        val targetPage = postCompletionPage ?: NotificationCleanerPage.Status
        cleaningCompletionPending = false
        postCompletionPage = null
        changeState {
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
        changeState {
            it.copy(
                isLoading = false,
                scanInProgress = false,
                isInitialized = true,
                errorMessage = error.message ?: appString(R.string.notification_status_load_failed),
            )
        }
    }

    private fun appString(resId: Int): String = message(resId)

    private fun loadNotificationAppsIfNeeded() {
        val current = currentState
        if (current.appsLoaded || current.isAppsLoading) return
        changeState { it.copy(isAppsLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                NotificationAppsSnapshot(
                    apps = repository.notificationApps(),
                    selectedPackages = repository.selectedNotificationPackages(),
                )
            }.onSuccess { snapshot ->
                changeState {
                    it.copy(
                        apps = snapshot.apps,
                        selectedPackages = snapshot.selectedPackages,
                        appsLoaded = true,
                        isAppsLoading = false,
                    )
                }
            }.onFailure { error ->
                changeState {
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
        emitEffect(
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
