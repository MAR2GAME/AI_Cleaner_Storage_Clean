package com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation

import com.aicleanerstorage.spaceclean.feature.applock.domain.AppLockMonitoringController
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockApp
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.ui.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction

class AppLockViewModel(

    private val repository: AppLockRepository,
    private val monitoringController: AppLockMonitoringController? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val permissions: PermissionGateway? = null
) : MviViewModel<AppLockUiState, AppLockIntent, AppLockEffect>(AppLockUiState(
            page = if (repository.isPinSet()) AppLockPage.Pin else AppLockPage.SelectApps,
            pinStep = if (repository.isPinSet()) AppLockPinStep.Verify else AppLockPinStep.Create,
            monitoringEnabled = repository.isMonitoringEnabled(),
            autoLockEnabled = repository.isAutoLockEnabled(),
            vibrationEnabled = repository.isVibrationEnabled(),
            isPinSet = repository.isPinSet(),
            overlayPermissionRequired = false
        )) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: AppLockIntent) {
        when (intent) {
            is AppLockIntent.Navigate -> emitEffect(AppLockEffect.Navigate(intent.destination))
            is AppLockIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            AppLockIntent.ReturnToManage -> returnToManage()
            AppLockIntent.LeavePinPage -> leavePinPage()
            is AppLockIntent.SetMonitoringEnabled -> guard(ProtectedAction.AppLockEnableMonitoring) { setMonitoringEnabled(intent.enabled) }
            is AppLockIntent.SetAutoLockEnabled -> setAutoLockEnabled(intent.enabled)
            is AppLockIntent.SetVibrationEnabled -> setVibrationEnabled(intent.enabled)
            else -> {
        if (intent is AppLockStateChanged) {
            updateState { AppLockReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: AppLockUiState) {
        synchronized(stateUpdateLock) { dispatch(AppLockStateChanged(value)) }
    }

    private fun changeState(transform: (AppLockUiState) -> AppLockUiState) {
        synchronized(stateUpdateLock) { dispatch(AppLockStateChanged(transform(currentState))) }
    }


    private var completePinJob: Job? = null

    init {
        loadApps()
        syncMonitoringService()
    }

    private fun handleIntent(action: AppLockIntent) {
        when (action) {
            AppLockIntent.Back -> if (!handleBack()) emitEffect(AppLockEffect.Exit)
            AppLockIntent.RefreshAfterResume -> refreshAfterResume()
            AppLockIntent.ConsumeToast -> consumeToast()
            AppLockIntent.ConsumeOverlayPermissionRequest -> guard(ProtectedAction.AppLockRequestOverlay) { consumeOverlayPermissionRequest() }
            AppLockIntent.OpenSettings -> openSettings()
            AppLockIntent.OpenSearch -> openSearch()
            AppLockIntent.ToggleAllApps -> guard(ProtectedAction.AppLockOpenProtectedArea) { toggleAllApps() }
            AppLockIntent.BeginCreatePin -> guard(ProtectedAction.AppLockOpenProtectedArea) { beginCreatePin() }
            AppLockIntent.RemovePinDigit -> removePinDigit()
            AppLockIntent.StartChangePin -> startChangePin()
            is AppLockIntent.TogglePackage -> guard(ProtectedAction.AppLockOpenProtectedArea) { togglePackage(action.packageName) }
            is AppLockIntent.AddPinDigit -> addPinDigit(action.digit)
            is AppLockIntent.SearchQueryChanged -> updateSearchQuery(action.query)
            is AppLockIntent.MonitoringChanged -> if (action.enabled) {
                guard(ProtectedAction.AppLockEnableMonitoring) { setMonitoringEnabled(true) }
            } else setMonitoringEnabled(false)
            is AppLockIntent.AutoLockChanged -> setAutoLockEnabled(action.enabled)
            is AppLockIntent.VibrationChanged -> setVibrationEnabled(action.enabled)
            is AppLockStateChanged -> Unit
            else -> Unit
        }
    }

    private fun guard(action: ProtectedAction, block: () -> Unit) {
        launchTask {
            when (permissions?.let { ActionPermissionGate(it).checkAction(action) } ?: GateResult.Allow) {
                GateResult.Allow -> block()
                is GateResult.Deny, GateResult.Busy, GateResult.Cancelled -> Unit
            }
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
        if (!digit.isDigit() || current.pinInput.length >= PIN_LENGTH) return
        val nextPin = current.pinInput + digit
        changeState { it.copy(pinInput = nextPin, pinErrorRes = null) }
        if (nextPin.length == PIN_LENGTH) {
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
        if (enabled) {
            syncMonitoringService()
        } else {
            monitoringController?.disableMonitoring() ?: repository.setMonitoringEnabled(false)
        }
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

    private fun processCompletePin(pin: String) {
        when (currentState.pinStep) {
            AppLockPinStep.Create -> {
                changeState {
                    it.copy(
                        pinStep = AppLockPinStep.CreateConfirm,
                        pinInput = "",
                        firstPin = pin,
                        pinErrorRes = null
                    )
                }
            }

            AppLockPinStep.CreateConfirm -> {
                if (pin == currentState.firstPin) {
                    val lockedPackages = currentState.apps
                        .filter { it.isLocked }
                        .map { it.packageName }
                        .toSet()
                    repository.savePin(pin)
                    repository.setLockedPackages(lockedPackages)
                    repository.setMonitoringEnabled(true)
                    changeState {
                        it.copy(
                            page = AppLockPage.Manage,
                            pinStep = AppLockPinStep.Create,
                            pinInput = "",
                            firstPin = "",
                            pinErrorRes = null,
                            monitoringEnabled = true,
                            isPinSet = true,
                            overlayPermissionRequired = !repository.hasOverlayPermission()
                        )
                    }
                    loadApps()
                    syncMonitoringService()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.Verify -> {
                if (repository.verifyPin(pin)) {
                    changeState {
                        it.copy(
                            page = AppLockPage.Manage,
                            pinInput = "",
                            pinErrorRes = null,
                            isPinSet = true,
                            overlayPermissionRequired = !repository.hasOverlayPermission()
                        )
                    }
                    loadApps()
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeVerify -> {
                if (repository.verifyPin(pin)) {
                    changeState {
                        it.copy(
                            pinStep = AppLockPinStep.ChangeNew,
                            pinInput = "",
                            firstPin = "",
                            pinErrorRes = null
                        )
                    }
                } else {
                    showPinError()
                }
            }

            AppLockPinStep.ChangeNew -> {
                changeState {
                    it.copy(
                        pinStep = AppLockPinStep.ChangeConfirm,
                        pinInput = "",
                        firstPin = pin,
                        pinErrorRes = null
                    )
                }
            }

            AppLockPinStep.ChangeConfirm -> {
                if (pin == currentState.firstPin) {
                    repository.savePin(pin)
                    changeState {
                        it.copy(
                            page = it.pinReturnPage,
                            pinInput = "",
                            firstPin = "",
                            pinErrorRes = null,
                            isPinSet = true,
                            overlayPermissionRequired = false,
                            toastRes = R.string.pin_updated_successfully
                        )
                    }
                } else {
                    showPinError()
                }
            }
        }
    }

    private fun showPinError() {
        changeState {
            it.copy(
                pinInput = "",
                pinErrorRes = R.string.pin_no_match
            )
        }
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

    private fun sortApps(apps: List<AppLockApp>): List<AppLockApp> =
        apps.sortedWith(
            compareBy<AppLockApp> { it.appName.lowercase() }
                .thenBy { it.packageName }
        )

    private fun syncMonitoringService() {
        monitoringController?.syncMonitoringService()
    }

    private fun shouldPromptOverlayPermission(page: AppLockPage): Boolean =
        page == AppLockPage.Manage &&
            currentState.overlayPermissionRequired &&
            !repository.hasOverlayPermission()

    companion object {
        private const val PIN_LENGTH = 4
        private const val PIN_COMPLETE_DISPLAY_DELAY_MS = 220L
    }
}
