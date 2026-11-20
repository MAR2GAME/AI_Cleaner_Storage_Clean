package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionChecker

import com.aicleanerstorage.spaceclean.domain.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.port.NotificationRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
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
    private val permissionChecker: PermissionChecker? = null,
) : MviViewModel<NotificationCleanerUiState, NotificationCleanerAction, NotificationCleanerEvent>(NotificationCleanerUiState()) {
    override fun onAction(intent: NotificationCleanerAction) {
        when (intent) {
            is NotificationCleanerAction.NavigationRequested -> sendEvent(NotificationCleanerEvent.Navigate(intent.destination))
            NotificationCleanerAction.StateRefreshRequested -> refreshState()
            else -> handleIntent(intent)
        }
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

    private fun handleIntent(action: NotificationCleanerAction) {
        when (action) {
            NotificationCleanerAction.BackClicked -> {
                when (currentState.page) {
                    NotificationCleanerPage.Settings -> leaveSettings()
                    NotificationCleanerPage.Scanning, NotificationCleanerPage.Cleaning -> requestStopDialog()
                    else -> sendEvent(NotificationCleanerEvent.Exit)
                }
            }
            NotificationCleanerAction.StopClicked -> requestStopDialog()
            NotificationCleanerAction.StopConfirmed -> { confirmStoppedOperationExit(); sendEvent(NotificationCleanerEvent.Exit) }
            NotificationCleanerAction.StopDismissed -> resumeStoppedOperation()
            NotificationCleanerAction.RefreshRequested -> refreshState()
            NotificationCleanerAction.SettingsClicked -> showSettings()
            NotificationCleanerAction.SettingsBackClicked -> leaveSettings()
            NotificationCleanerAction.BlockingEnableRequested -> guardEnableBlocking()
            NotificationCleanerAction.BlockingDisableRequested -> setBlockingEnabled(false)
            NotificationCleanerAction.TidyUpClicked -> tidyUp()
            NotificationCleanerAction.ScanAnimationFinished -> finishScanning()
            NotificationCleanerAction.CleaningAnimationFinished -> finishCleaning()
            NotificationCleanerAction.CompletionAnimationFinished -> finishCompleteAnimation()
            is NotificationCleanerAction.StatusRequested -> showStatus(action.scanGeneration)
            is NotificationCleanerAction.PackageToggled -> togglePackage(action.packageName)
            else -> Unit
        }
    }

    private fun guardEnableBlocking() {
        launch {
            if (permissionChecker?.isGranted(AppPermission.NotificationListener) != false) {
                setBlockingEnabled(true)
            } else {
                changeState { it.copy(hasAccess = false, enabled = false) }
            }
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
        changeState { NotificationCleanerReducer.scanCompleted(it) }
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
        changeState { NotificationCleanerReducer.scanCancelled(it) }
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
        runCatching { repository.notificationSnapshot(includeApps = false) }
            .onSuccess { snapshot ->
                if (!snapshot.hasAccess) {
                    handleAccessRefresh(false)
                    return@onSuccess
                }
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
        changeState { NotificationCleanerReducer.scanStarted(it) }
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
                    changeState { NotificationCleanerReducer.notificationsCleaned(it, cleanedCount) }
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
        changeState { NotificationCleanerReducer.cleaningFinished(it) }
    }

    private fun finishCompleteAnimation() {
        if (currentState.page != NotificationCleanerPage.Complete || !cleaningCompletionPending) return
        cleaningCompletionPending = false
        showStatusAfterCompletionAd()
    }

    private fun showStatusAfterCompletionAd() {
        val targetPage = postCompletionPage ?: NotificationCleanerPage.Status
        cleaningCompletionPending = false
        postCompletionPage = null
        changeState { NotificationCleanerReducer.completionDisplayed(it, targetPage) }
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
