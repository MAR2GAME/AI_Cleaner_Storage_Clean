package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.rule.AppLockPinRule
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
) : MviViewModel<AppLockUiState, AppLockAction, AppLockEvent>(
        AppLockUiState(
            page = if (repository.isPinSet()) AppLockPage.Pin else AppLockPage.SelectApps,
            pinStep = if (repository.isPinSet()) AppLockPinStep.Verify else AppLockPinStep.Create,
            monitoringEnabled = repository.isMonitoringEnabled(),
            autoLockEnabled = repository.isAutoLockEnabled(),
            vibrationEnabled = repository.isVibrationEnabled(),
            isPinSet = repository.isPinSet(),
        ),
    ) {
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
            AppLockAction.SettingsClicked -> openSettings()
            AppLockAction.SearchClicked -> openSearch()
            AppLockAction.AllAppsSelectionToggled -> toggleAllApps()
            AppLockAction.PinCreationRequested -> beginCreatePin()
            AppLockAction.PinBackspaceClicked -> removePinDigit()
            AppLockAction.ChangePinClicked -> startChangePin()
            is AppLockAction.PackageToggled -> togglePackage(action.packageName)
            is AppLockAction.PinDigitClicked -> addPinDigit(action.digit)
            is AppLockAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is AppLockAction.MonitoringChanged ->
                if (!action.enabled) {
                    setMonitoringEnabled(false)
                } else if (currentState.missingPermissions.isEmpty()) {
                    setMonitoringEnabled(true)
                } else {
                    // 权限缺失时保持关闭，Manage 页会显示权限行
                    setState { it.copy(monitoringEnabled = false) }
                }

            is AppLockAction.PermissionClicked ->
                sendEvent(AppLockEvent.OpenPermissionSettings(action.permission))

            is AppLockAction.StatusesChanged ->
                setState { it.copy(statuses = action.statuses) }
            is AppLockAction.AutoLockChanged -> setAutoLockEnabled(action.enabled)
            is AppLockAction.VibrationChanged -> setVibrationEnabled(action.enabled)
        }
    }

    private fun handleBack(): Boolean =
        when (currentState.page) {
            AppLockPage.Search,
            AppLockPage.Settings,
            -> {
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
        setState {
            it.copy(
                monitoringEnabled = repository.isMonitoringEnabled(),
                autoLockEnabled = repository.isAutoLockEnabled(),
                vibrationEnabled = repository.isVibrationEnabled(),
                isPinSet = repository.isPinSet(),
            )
        }
        loadApps()
        syncMonitoringService()
    }

    private fun updateSearchQuery(query: String) {
        setState { it.copy(searchQuery = query) }
    }

    private fun openSearch() {
        setState { it.copy(page = AppLockPage.Search, searchQuery = "") }
    }

    private fun openSettings() {
        setState { it.copy(page = AppLockPage.Settings) }
    }

    private fun returnToManage() {
        setState {
            it.copy(
                page = AppLockPage.Manage,
                searchQuery = "",
                pinInput = "",
                firstPin = "",
                pinErrorRes = null,
            )
        }
    }

    private fun togglePackage(packageName: String) {
        val current = currentState
        val target = current.apps.firstOrNull { it.packageName == packageName } ?: return
        if (!target.isLocked &&
            current.page in setOf(AppLockPage.Manage, AppLockPage.Search) &&
            current.missingPermissions.isNotEmpty()
        ) {
            sendEvent(AppLockEvent.OpenPermissionSettings(current.missingPermissions.first()))
            return
        }
        val updatedApps =
            current.apps.map { app ->
                if (app.packageName == packageName) app.copy(isLocked = !app.isLocked) else app
            }
        setState { it.copy(apps = updatedApps) }
        if (current.page == AppLockPage.Manage || current.page == AppLockPage.Search) {
            updatedApps.firstOrNull { it.packageName == packageName }?.let {
                repository.setPackageLocked(packageName, it.isLocked)
            }
            syncMonitoringService()
        }
    }

    private fun toggleAllApps() {
        val lock = !currentState.allAppsLocked
        if (lock && currentState.missingPermissions.isNotEmpty()) {
            sendEvent(AppLockEvent.OpenPermissionSettings(currentState.missingPermissions.first()))
            return
        }
        val updatedApps = currentState.apps.map { it.copy(isLocked = lock) }
        setState { it.copy(apps = updatedApps) }
        if (currentState.page == AppLockPage.Manage) {
            repository.setLockedPackages(updatedApps.filter { it.isLocked }.map { it.packageName }.toSet())
            syncMonitoringService()
        }
    }

    private fun beginCreatePin() {
        if (!currentState.hasSelectedApps) return
        val missing = currentState.missingPermissions.firstOrNull()
        if (missing != null) {
            sendEvent(AppLockEvent.OpenPermissionSettings(missing))
            return
        }
        cancelCompletePinJob()
        setState {
            it.copy(
                page = AppLockPage.Pin,
                pinStep = AppLockPinStep.Create,
                pinInput = "",
                firstPin = "",
                pinReturnPage = AppLockPage.Manage,
                pinErrorRes = null,
            )
        }
    }

    private fun startChangePin() {
        cancelCompletePinJob()
        setState {
            it.copy(
                page = AppLockPage.Pin,
                pinStep = AppLockPinStep.ChangeVerify,
                pinInput = "",
                firstPin = "",
                pinReturnPage = AppLockPage.Settings,
                pinErrorRes = null,
            )
        }
    }

    private fun addPinDigit(digit: Char) {
        val current = currentState
        if (!digit.isDigit() || current.pinInput.length >= AppLockPinRule.PIN_LENGTH) return
        val nextPin = current.pinInput + digit
        setState { it.copy(pinInput = nextPin, pinErrorRes = null) }
        if (nextPin.length == AppLockPinRule.PIN_LENGTH) {
            completePinJob?.cancel()
            completePinJob =
                viewModelScope.launch {
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
        setState { it.copy(pinInput = it.pinInput.dropLast(1), pinErrorRes = null) }
    }

    private fun leavePinPage() {
        cancelCompletePinJob()
        val current = currentState
        val targetPage =
            when {
                current.pinStep == AppLockPinStep.Verify && repository.isPinSet() -> AppLockPage.Pin
                current.pinStep == AppLockPinStep.ChangeVerify ||
                    current.pinStep == AppLockPinStep.ChangeNew ||
                    current.pinStep == AppLockPinStep.ChangeConfirm -> current.pinReturnPage
                repository.isPinSet() -> AppLockPage.Manage
                else -> AppLockPage.SelectApps
            }
        val targetStep = if (targetPage == AppLockPage.Pin) AppLockPinStep.Verify else AppLockPinStep.Create
        setState {
            it.copy(
                page = targetPage,
                pinStep = targetStep,
                pinInput = "",
                firstPin = "",
                pinErrorRes = null,
                isPinSet = repository.isPinSet(),
            )
        }
    }

    private fun setMonitoringEnabled(enabled: Boolean) {
        repository.setMonitoringEnabled(enabled)
        setState { it.copy(monitoringEnabled = enabled) }
        syncMonitoringService()
    }

    private fun setAutoLockEnabled(enabled: Boolean) {
        repository.setAutoLockEnabled(enabled)
        setState { it.copy(autoLockEnabled = enabled) }
    }

    private fun setVibrationEnabled(enabled: Boolean) {
        repository.setVibrationEnabled(enabled)
        setState { it.copy(vibrationEnabled = enabled) }
    }

    private fun consumeToast() {
        setState { it.copy(toastRes = null) }
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
                setState { AppLockReducer.firstPinEntered(it, pin) }
            }

            AppLockPinStep.CreateConfirm -> {
                if (pin == currentState.firstPin) {
                    val lockedPackages =
                        currentState.apps
                            .filter { it.isLocked }
                            .map { it.packageName }
                            .toSet()
                    withContext(ioDispatcher) { repository.savePin(pin) }
                    repository.setLockedPackages(lockedPackages)
                    repository.setMonitoringEnabled(true)
                    setState { AppLockReducer.pinCreated(it) }
                    loadApps()
                    syncMonitoringService()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.Verify -> {
                if (withContext(ioDispatcher) { repository.verifyPin(pin) }) {
                    setState { AppLockReducer.pinVerified(it) }
                    loadApps()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeVerify -> {
                if (withContext(ioDispatcher) { repository.verifyPin(pin) }) {
                    setState { AppLockReducer.oldPinVerified(it) }
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeNew -> {
                setState { AppLockReducer.newPinEntered(it, pin) }
            }

            AppLockPinStep.ChangeConfirm -> {
                if (pin == currentState.firstPin) {
                    withContext(ioDispatcher) { repository.savePin(pin) }
                    setState { AppLockReducer.pinChanged(it) }
                } else {
                    showPinError()
                }
            }
        }
    }

    private fun showPinError() {
        setState { AppLockReducer.pinRejected(it) }
    }

    private fun loadApps() {
        viewModelScope.launch {
            val existingLocks = currentState.apps.associate { it.packageName to it.isLocked }
            setState { it.copy(isLoading = true) }
            val apps =
                try {
                    withContext(ioDispatcher) { repository.lockableApps() }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    setState { state -> state.copy(isLoading = false) }
                    return@launch
                }
            val mergedApps =
                if (!repository.isPinSet() && existingLocks.isNotEmpty()) {
                    apps.map { app -> existingLocks[app.packageName]?.let { app.copy(isLocked = it) } ?: app }
                } else {
                    apps
                }
            setState { it.copy(apps = sortApps(mergedApps), isLoading = false) }
        }
    }

    private fun sortApps(apps: List<AppLockAppModel>): List<AppLockAppModel> =
        apps.sortedWith(
            compareBy<AppLockAppModel> { it.appName.lowercase() }
                .thenBy { it.packageName },
        )

    companion object {
        private const val PIN_COMPLETE_DISPLAY_DELAY_MS = 220L
    }
}
