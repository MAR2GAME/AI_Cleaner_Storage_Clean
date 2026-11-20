package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.data.applock.AppLockPinRule

import com.aicleanerstorage.spaceclean.data.applock.model.AppLockAppModel
import com.aicleanerstorage.spaceclean.data.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionChecker
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppLockViewModel(

    private val repository: AppLockRepository,
    private val syncMonitoringService: () -> Unit,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val permissionChecker: PermissionChecker? = null,
) : MviViewModel<AppLockUiState, AppLockAction, AppLockEvent>(AppLockUiState(
            page = if (repository.isPinSet()) AppLockPage.Pin else AppLockPage.SelectApps,
            pinStep = if (repository.isPinSet()) AppLockPinStep.Verify else AppLockPinStep.Create,
            monitoringEnabled = repository.isMonitoringEnabled(),
            autoLockEnabled = repository.isAutoLockEnabled(),
            vibrationEnabled = repository.isVibrationEnabled(),
            isPinSet = repository.isPinSet(),
            overlayPermissionRequired = false
        )) {
    private var completePinJob: Job? = null

    init {
        loadApps()
        syncMonitoringService()
    }

    override fun onAction(action: AppLockAction) {
        when (action) {
            is AppLockAction.NavigationRequested -> sendEvent(AppLockEvent.Navigate(action.destination))
            AppLockAction.BackClicked -> if (!handleBack()) sendEvent(AppLockEvent.Exit)
            AppLockAction.Resumed -> refreshAfterResume()
            AppLockAction.ToastShown -> consumeToast()
            AppLockAction.OverlayPermissionRequestHandled -> guard(listOf(AppPermission.Overlay)) { consumeOverlayPermissionRequest() }
            AppLockAction.SettingsClicked -> openSettings()
            AppLockAction.SearchClicked -> openSearch()
            AppLockAction.AllAppsSelectionToggled -> guard(listOf(AppPermission.UsageAccess)) { toggleAllApps() }
            AppLockAction.PinCreationRequested -> guard(listOf(AppPermission.UsageAccess)) { beginCreatePin() }
            AppLockAction.PinBackspaceClicked -> removePinDigit()
            AppLockAction.ChangePinClicked -> startChangePin()
            is AppLockAction.PackageToggled -> guard(listOf(AppPermission.UsageAccess)) { togglePackage(action.packageName) }
            is AppLockAction.PinDigitClicked -> addPinDigit(action.digit)
            is AppLockAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is AppLockAction.MonitoringChanged -> if (action.enabled) {
                guard(listOf(AppPermission.UsageAccess, AppPermission.Overlay)) { setMonitoringEnabled(true) }
            } else setMonitoringEnabled(false)
            is AppLockAction.AutoLockChanged -> setAutoLockEnabled(action.enabled)
            is AppLockAction.VibrationChanged -> setVibrationEnabled(action.enabled)
        }
    }

    private fun guard(required: List<AppPermission>, block: () -> Unit) {
        launch {
            if (required.all { permissionChecker?.isGranted(it) != false }) block()
        }
    }

    private fun handleBack(): Boolean =
        when (currentState.page) {
            AppLockPage.Search,
            AppLockPage.Settings -> {
                returnToManage()
                true
            }
            AppLockPage.Pin -> {
                if (currentState.pinStep == AppLockPinStep.Verify) {
                    false
                } else {
                    leavePinPage()
                    true
                }
            }
            else -> false
        }

    private fun refreshAfterResume() {
        changeState {
            it.copy(
                monitoringEnabled = repository.isMonitoringEnabled(),
                autoLockEnabled = repository.isAutoLockEnabled(),
                vibrationEnabled = repository.isVibrationEnabled(),
                isPinSet = repository.isPinSet(),
                overlayPermissionRequired = shouldPromptOverlayPermission(page = it.page)
            )
        }
        loadApps()
        syncMonitoringService()
    }

    private fun updateSearchQuery(query: String) {
        changeState { it.copy(searchQuery = query) }
    }

    private fun openSearch() {
        changeState { it.copy(page = AppLockPage.Search, searchQuery = "") }
    }

    private fun openSettings() {
        changeState { it.copy(page = AppLockPage.Settings) }
    }

    private fun returnToManage() {
        changeState {
            it.copy(
                page = AppLockPage.Manage,
                searchQuery = "",
                pinInput = "",
                firstPin = "",
                pinErrorRes = null,
                overlayPermissionRequired = false
            )
        }
    }

    private fun togglePackage(packageName: String) {
        val current = currentState
        val updatedApps = current.apps.map { app ->
            if (app.packageName == packageName) app.copy(isLocked = !app.isLocked) else app
        }
        changeState { it.copy(apps = updatedApps) }
        if (current.page == AppLockPage.Manage || current.page == AppLockPage.Search) {
            updatedApps.firstOrNull { it.packageName == packageName }?.let {
                repository.setPackageLocked(packageName, it.isLocked)
            }
            syncMonitoringService()
        }
    }

    private fun toggleAllApps() {
        val lock = !currentState.allAppsLocked
        val updatedApps = currentState.apps.map { it.copy(isLocked = lock) }
        changeState { it.copy(apps = updatedApps) }
        if (currentState.page == AppLockPage.Manage) {
            repository.setLockedPackages(updatedApps.filter { it.isLocked }.map { it.packageName }.toSet())
            syncMonitoringService()
        }
    }

    private fun beginCreatePin() {
        if (!currentState.hasSelectedApps) return
        cancelCompletePinJob()
        changeState {
            it.copy(
                page = AppLockPage.Pin,
                pinStep = AppLockPinStep.Create,
                pinInput = "",
                firstPin = "",
                pinReturnPage = AppLockPage.Manage,
                pinErrorRes = null,
                overlayPermissionRequired = false
            )
        }
    }

    private fun consumeOverlayPermissionRequest() {
        changeState { it.copy(overlayPermissionRequired = false) }
    }

    private fun startChangePin() {
        cancelCompletePinJob()
        changeState {
            it.copy(
                page = AppLockPage.Pin,
                pinStep = AppLockPinStep.ChangeVerify,
                pinInput = "",
                firstPin = "",
                pinReturnPage = AppLockPage.Settings,
                pinErrorRes = null
            )
        }
    }

    private fun addPinDigit(digit: Char) {
        val current = currentState
        if (!digit.isDigit() || current.pinInput.length >= AppLockPinRule.PIN_LENGTH) return
        val nextPin = current.pinInput + digit
        changeState { it.copy(pinInput = nextPin, pinErrorRes = null) }
        if (nextPin.length == AppLockPinRule.PIN_LENGTH) {
            completePinJob?.cancel()
            completePinJob = viewModelScope.launch {
                delay(PIN_COMPLETE_DISPLAY_DELAY_MS)
                if (currentState.pinInput == nextPin) {
                    processCompletePin(nextPin)
                }
                completePinJob = null
            }
        }
    }

    private fun removePinDigit() {
        cancelCompletePinJob()
        changeState { it.copy(pinInput = it.pinInput.dropLast(1), pinErrorRes = null) }
    }

    private fun leavePinPage() {
        cancelCompletePinJob()
        val current = currentState
        val targetPage = when {
            current.pinStep == AppLockPinStep.Verify && repository.isPinSet() -> AppLockPage.Pin
            current.pinStep == AppLockPinStep.ChangeVerify ||
                current.pinStep == AppLockPinStep.ChangeNew ||
                current.pinStep == AppLockPinStep.ChangeConfirm -> current.pinReturnPage
            repository.isPinSet() -> AppLockPage.Manage
            else -> AppLockPage.SelectApps
        }
        val targetStep = if (targetPage == AppLockPage.Pin) AppLockPinStep.Verify else AppLockPinStep.Create
        changeState {
            it.copy(
                page = targetPage,
                pinStep = targetStep,
                pinInput = "",
                firstPin = "",
                pinErrorRes = null,
                isPinSet = repository.isPinSet(),
                overlayPermissionRequired = false
            )
        }
    }

    private fun setMonitoringEnabled(enabled: Boolean) {
        repository.setMonitoringEnabled(enabled)
        changeState { it.copy(monitoringEnabled = enabled) }
        syncMonitoringService()
    }

    private fun setAutoLockEnabled(enabled: Boolean) {
        repository.setAutoLockEnabled(enabled)
        changeState { it.copy(autoLockEnabled = enabled) }
    }

    private fun setVibrationEnabled(enabled: Boolean) {
        repository.setVibrationEnabled(enabled)
        changeState { it.copy(vibrationEnabled = enabled) }
    }

    private fun consumeToast() {
        changeState { it.copy(toastRes = null) }
    }

    override fun onCleared() {
        cancelCompletePinJob()
        super.onCleared()
    }

    private fun cancelCompletePinJob() {
        completePinJob?.cancel()
        completePinJob = null
    }

    private suspend fun processCompletePin(pin: String) {
        when (currentState.pinStep) {
            AppLockPinStep.Create -> {
                changeState { AppLockReducer.firstPinEntered(it, pin) }
            }

            AppLockPinStep.CreateConfirm -> {
                if (pin == currentState.firstPin) {
                    val lockedPackages = currentState.apps
                        .filter { it.isLocked }
                        .map { it.packageName }
                        .toSet()
                    withContext(ioDispatcher) { repository.savePin(pin) }
                    repository.setLockedPackages(lockedPackages)
                    repository.setMonitoringEnabled(true)
                    changeState { AppLockReducer.pinCreated(it, !repository.hasOverlayPermission()) }
                    loadApps()
                    syncMonitoringService()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.Verify -> {
                if (withContext(ioDispatcher) { repository.verifyPin(pin) }) {
                    changeState { AppLockReducer.pinVerified(it, !repository.hasOverlayPermission()) }
                    loadApps()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeVerify -> {
                if (withContext(ioDispatcher) { repository.verifyPin(pin) }) {
                    changeState { AppLockReducer.oldPinVerified(it) }
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeNew -> {
                changeState { AppLockReducer.newPinEntered(it, pin) }
            }

            AppLockPinStep.ChangeConfirm -> {
                if (pin == currentState.firstPin) {
                    withContext(ioDispatcher) { repository.savePin(pin) }
                    changeState { AppLockReducer.pinChanged(it) }
                } else {
                    showPinError()
                }
            }
        }
    }

    private fun showPinError() {
        changeState { AppLockReducer.pinRejected(it) }
    }

    private fun loadApps() {
        viewModelScope.launch {
            val existingLocks = currentState.apps.associate { it.packageName to it.isLocked }
            changeState { it.copy(isLoading = true) }
            val apps = try {
                withContext(ioDispatcher) { repository.lockableApps() }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                changeState { state -> state.copy(isLoading = false) }
                return@launch
            }
            val mergedApps = if (!repository.isPinSet() && existingLocks.isNotEmpty()) {
                apps.map { app -> existingLocks[app.packageName]?.let { app.copy(isLocked = it) } ?: app }
            } else {
                apps
            }
            changeState { it.copy(apps = sortApps(mergedApps), isLoading = false) }
        }
    }

    private fun sortApps(apps: List<AppLockAppModel>): List<AppLockAppModel> =
        apps.sortedWith(
            compareBy<AppLockAppModel> { it.appName.lowercase() }
                .thenBy { it.packageName }
        )

    private fun shouldPromptOverlayPermission(page: AppLockPage): Boolean =
        page == AppLockPage.Manage &&
            currentState.overlayPermissionRequired &&
            !repository.hasOverlayPermission()

    companion object {
        private const val PIN_COMPLETE_DISPLAY_DELAY_MS = 220L
    }
}
