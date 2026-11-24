package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
import com.aicleanerstorage.spaceclean.domain.model.notification.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.notification.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.port.NotificationCleanerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationCleanerViewModel(
    private val repository: NotificationCleanerRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NotificationCleanerUiState, NotificationCleanerAction, NotificationCleanerEvent>(NotificationCleanerUiState()) {
    private var scanJob: Job? = null
    private var activeScanGeneration = 0L
    private var pendingScanCompletionGeneration: Long? = null
    private var deferredScanCompletionGeneration: Long? = null
    private var cleaningCompletionPending = false
    private var postCompletionPage: NotificationCleanerPage? = null

    init {
        refreshState()
    }

    override fun onAction(action: NotificationCleanerAction) {
        when (action) {
            is NotificationCleanerAction.NavigationRequested -> sendEvent(NotificationCleanerEvent.Navigate(action.destination))
            NotificationCleanerAction.BackClicked -> {
                when (currentState.page) {
                    NotificationCleanerPage.Settings -> leaveSettings()
                    NotificationCleanerPage.Scanning, NotificationCleanerPage.Cleaning -> requestStopDialog()
                    else -> sendEvent(NotificationCleanerEvent.Exit)
                }
            }
            NotificationCleanerAction.StopConfirmed -> {
                confirmStoppedOperationExit()
                sendEvent(NotificationCleanerEvent.Exit)
            }
            NotificationCleanerAction.StopDismissed -> resumeStoppedOperation()
            NotificationCleanerAction.RefreshRequested -> refreshState()
            NotificationCleanerAction.SettingsClicked -> showSettings()
            NotificationCleanerAction.BlockingEnableRequested -> setBlockingEnabled(true)
            NotificationCleanerAction.BlockingDisableRequested -> setBlockingEnabled(false)
            NotificationCleanerAction.TidyUpClicked -> tidyUp()
            NotificationCleanerAction.CleaningAnimationFinished -> finishCleaning()
            NotificationCleanerAction.CompletionAnimationFinished -> finishCompleteAnimation()
            is NotificationCleanerAction.StatusRequested -> showStatus(action.scanGeneration)
            is NotificationCleanerAction.PackageToggled -> togglePackage(action.packageName)
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

    private fun setBlockingEnabled(checked: Boolean) {
        if (checked && !repository.hasNotificationListenerAccess()) {
            // Route 已保证授权；这里兜住并发，让页面回到 Onboarding 重新引导，而不是静默成功。
            refreshState()
            return
        }

        repository.setNotificationBlockingEnabled(checked)
        refreshState()
    }

    private fun togglePackage(packageName: String) {
        if (!currentState.enabled) return
        val selected = packageName !in currentState.selectedPackages
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                repository.setNotificationPackageSelected(packageName, selected)
                repository.selectedNotificationPackages()
            }.onSuccess { selectedPackages ->
                setState { it.copy(selectedPackages = selectedPackages, error = null) }
            }.onFailure { error ->
                setState {
                    it.copy(error = UiText.Res(R.string.notification_app_selection_update_failed))
                }
            }
        }
    }

    private fun showSettings() {
        setState { it.copy(page = NotificationCleanerPage.Settings) }
        loadNotificationAppsIfNeeded()
    }

    private fun leaveSettings() {
        setState { it.copy(page = NotificationCleanerPage.Status) }
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
        setState { NotificationCleanerReducer.scanCompleted(it) }
    }

    private fun requestStopDialog() {
        val stoppedPage = currentState.page
        if (
            stoppedPage != NotificationCleanerPage.Scanning &&
            stoppedPage != NotificationCleanerPage.Cleaning
        ) {
            return
        }
        setState {
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
        setState { it.copy(showStopDialog = false, stopDialogPage = null) }
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
        setState { it.copy(showStopDialog = false, stopDialogPage = null) }
    }

    private fun cancelActiveScan() {
        activeScanGeneration += 1L
        pendingScanCompletionGeneration = null
        deferredScanCompletionGeneration = null
        scanJob?.cancel()
        scanJob = null
        setState { NotificationCleanerReducer.scanCancelled(it) }
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
            resetForNoAccess()
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

    private fun resetForNoAccess() {
        setState {
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
                error = null,
                cleanedCount = 0,
                cleaningAnimationComplete = false,
                completionResultVisible = false,
                showStopDialog = false,
                stopDialogPage = null,
            )
        }
    }

    private fun refreshLightState() {
        runCatching { repository.notificationSnapshot(includeApps = false) }
            .onSuccess { snapshot ->
                if (!snapshot.hasAccess) {
                    handleAccessRefresh(false)
                    return@onSuccess
                }
                setState { state ->
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
                        error = null,
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
        setState { NotificationCleanerReducer.scanStarted(it) }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                val startedAtMillis = System.currentTimeMillis()
                try {
                    val snapshot = repository.notificationSnapshot()
                    val remainingMillis = MIN_SCAN_DURATION_MILLIS - (System.currentTimeMillis() - startedAtMillis)
                    if (remainingMillis > 0L) delay(remainingMillis)
                    if (scanGeneration != activeScanGeneration) return@launch
                    applyScanSnapshot(snapshot)
                    if (scanGeneration != activeScanGeneration) return@launch
                    scanJob = null
                    finishScanIfReady(scanGeneration)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    if (scanGeneration != activeScanGeneration) return@launch
                    scanJob = null
                    setState {
                        it.copy(
                            isLoading = false,
                            scanInProgress = false,
                            isInitialized = true,
                            error = UiText.Res(R.string.notification_status_load_failed),
                        )
                    }
                }
            }
    }

    private fun applyScanSnapshot(snapshot: NotificationCleanerSnapshotModel) {
        if (!snapshot.hasAccess) {
            applyNoAccessSnapshot()
        } else {
            applyAccessSnapshot(snapshot)
        }
    }

    private fun applyNoAccessSnapshot() {
        setState {
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
                error = null,
            )
        }
    }

    private fun applyAccessSnapshot(snapshot: NotificationCleanerSnapshotModel) {
        setState {
            it.copy(
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
                error = null,
            )
        }
    }

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
        dispatch(NotificationCleanerAction.StatusRequested(scanGeneration))
    }

    private fun tidyUp() {
        val state = currentState
        if (state.page != NotificationCleanerPage.Status || state.blockedCount <= 0) return
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching { repository.clearBlockedNotifications() }
                .onSuccess { cleanedCount ->
                    if (cleanedCount <= 0) {
                        refreshState()
                        return@onSuccess
                    }
                    postCompletionPage = null
                    cleaningCompletionPending = false
                    setState { NotificationCleanerReducer.notificationsCleaned(it, cleanedCount) }
                }.onFailure { error ->
                    setState {
                        it.copy(error = UiText.Res(R.string.notification_status_load_failed))
                    }
                }
        }
    }

    private fun finishCleaning() {
        if (currentState.page != NotificationCleanerPage.Cleaning || cleaningCompletionPending) return
        cleaningCompletionPending = true
        postCompletionPage = NotificationCleanerPage.Complete
        setState { NotificationCleanerReducer.cleaningFinished(it) }
    }

    private fun finishCompleteAnimation() {
        if (currentState.page != NotificationCleanerPage.Complete || !cleaningCompletionPending) return
        cleaningCompletionPending = false
        showStatusAfterCompleteAnimation()
    }

    private fun showStatusAfterCompleteAnimation() {
        val targetPage = postCompletionPage ?: NotificationCleanerPage.Status
        cleaningCompletionPending = false
        postCompletionPage = null
        setState { NotificationCleanerReducer.completionDisplayed(it, targetPage) }
    }

    private fun failRefresh(error: Throwable) {
        setState {
            it.copy(
                isLoading = false,
                scanInProgress = false,
                isInitialized = true,
                error = UiText.Res(R.string.notification_status_load_failed),
            )
        }
    }

    private fun loadNotificationAppsIfNeeded() {
        val current = currentState
        if (current.appsLoaded || current.isAppsLoading) return
        setState { it.copy(isAppsLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                NotificationAppsSnapshot(
                    apps = repository.notificationApps(),
                    selectedPackages = repository.selectedNotificationPackages(),
                )
            }.onSuccess { snapshot ->
                setState {
                    it.copy(
                        apps = snapshot.apps,
                        selectedPackages = snapshot.selectedPackages,
                        appsLoaded = true,
                        isAppsLoading = false,
                    )
                }
            }.onFailure { error ->
                setState {
                    it.copy(
                        isAppsLoading = false,
                        error = UiText.Res(R.string.notification_status_load_failed),
                    )
                }
            }
        }
    }

    override fun onCleared() {
        scanJob?.cancel()
        super.onCleared()
    }

    private companion object {
        private const val MIN_SCAN_DURATION_MILLIS = 1_800L
    }
}

private data class NotificationAppsSnapshot(
    val apps: List<BlockableNotificationAppModel>,
    val selectedPackages: Set<String>,
)
