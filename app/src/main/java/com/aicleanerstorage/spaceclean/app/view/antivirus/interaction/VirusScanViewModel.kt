package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.error.VirusScanError
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.port.VirusSecurityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

private const val QUICK_SCAN_CALLBACK_GRACE_MILLIS = 5_000L
private const val DEEP_SCAN_CALLBACK_GRACE_MILLIS = 10_000L
private const val INSTALLED_APPS_DIALOG_INTERACTION_THRESHOLD_MILLIS = 1_500L

private enum class ScanTerminalState {
    Running,
    Success,
    Failure,
}

class VirusScanViewModel(
    private val scanEngine: VirusScanner,
    private val securityRepository: VirusSecurityRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : MviViewModel<VirusScanUiState, VirusScanAction, VirusScanEvent>(VirusScanUiState()) {
    private var resultVisible = false
    private var emptyResultSent = false

    private fun checkEmptyResult() {
        if (resultVisible && currentState.effectiveThreatCount == 0 && !emptyResultSent) {
            emptyResultSent = true
            sendEvent(VirusScanEvent.NavigateToResult(false))
        }
    }

    private var completionJob: Job? = null
    private var display: VirusScanDisplay? = null
    private var terminalTimeoutJob: Job? = null
    private var adbRiskJob: Job? = null
    private var scanStartedAt: Long = 0L
    private var scanGeneration = 0
    private var scanTerminalState = ScanTerminalState.Running
    private val threatIds = mutableSetOf<String>()
    private val pendingThreatJobs = ConcurrentHashMap.newKeySet<Job>()

    override fun onAction(action: VirusScanAction) {
        setState { VirusScanReducer.reduce(it, action) }
        when (action) {
            VirusScanAction.ResultBackClicked -> sendEvent(VirusScanEvent.NavigateBack)
            VirusScanAction.ResultEntered -> {
                resultVisible = true
                emptyResultSent = false
                checkEmptyResult()
            }
            VirusScanAction.ResultLeft -> resultVisible = false
            VirusScanAction.AdbRiskResolutionClicked -> sendEvent(VirusScanEvent.OpenDeveloperSettings)
            is VirusScanAction.AppThreatResolutionClicked -> sendEvent(VirusScanEvent.OpenAppSettings(action.packageName))
            is VirusScanAction.FileThreatDeleteClicked -> sendEvent(VirusScanEvent.DeleteFile(action.path))
            is VirusScanAction.NavigationRequested -> sendEvent(VirusScanEvent.Navigate(action.destination))
            VirusScanAction.AdbRiskRefreshRequested -> refreshAdbRisk()
            is VirusScanAction.PackageThreatRemovalRequested -> {
                threatIds.removeAll { it == action.packageName || it.endsWith(":" + action.packageName) }
                checkEmptyResult()
            }
            is VirusScanAction.FileThreatRemovalRequested -> {
                threatIds.remove("file:" + action.path)
                checkEmptyResult()
            }
            is VirusScanUpdate.AdbRiskChecked -> checkEmptyResult()
            is VirusScanAction.ScanStartRequested -> startScan(action.mode)
            VirusScanAction.ScanCancellationRequested -> cancelScan()
            VirusScanAction.Entered -> {
                if (!securityRepository.isScanNoticeAccepted()) {
                    dispatch(VirusScanUpdate.ScanNoticeShown)
                }
            }
            VirusScanAction.BackClicked -> sendEvent(VirusScanEvent.Exit)
            is VirusScanAction.ScanRequested -> requestHomeScan(action.mode)
            VirusScanAction.ScanNoticeAccepted -> {
                securityRepository.setScanNoticeAccepted()
                val mode = currentState.home.pendingScanMode
                if (mode != null) checkInstalledAppsAccess(mode, returningFromSettings = false)
            }
            VirusScanAction.ScanNoticeDeclined -> {
                sendEvent(VirusScanEvent.Exit)
            }
            VirusScanAction.InstalledAppsSettingsRequested -> sendEvent(VirusScanEvent.OpenInstalledAppsSettings)
            VirusScanAction.Resumed -> {
                val state = currentState.home
                val mode = state.pendingScanMode
                if (state.waitingForSettingsReturn && mode != null) {
                    checkInstalledAppsAccess(mode, returningFromSettings = true)
                }
            }
            is VirusScanAction.DeepScanPermissionResultReceived -> {
                val mode = currentState.home.pendingScanMode ?: VirusScanMode.Deep
                if (action.granted) {
                    sendEvent(VirusScanEvent.NavigateToScan(mode))
                }
            }
            is VirusScanUpdate, VirusScanAction.InstalledAppsDialogDismissed,
            VirusScanAction.SettingsLaunchFailed,
            -> Unit
        }
    }

    private fun requestHomeScan(mode: VirusScanMode) {
        if (currentState.home.scanRequestPending) return
        if (!securityRepository.isScanNoticeAccepted()) {
            dispatch(VirusScanUpdate.ScanNoticeShown)
            return
        }
        checkInstalledAppsAccess(mode, returningFromSettings = false)
    }

    private fun checkInstalledAppsAccess(
        mode: VirusScanMode,
        returningFromSettings: Boolean,
    ) {
        if (currentState.home.scanRequestPending) return
        dispatch(VirusScanUpdate.AccessCheckStarted(mode))
        viewModelScope.launch {
            val hadPreviousFailure = securityRepository.hasInstalledAppsAccessFailedBefore()
            val startedAt = System.currentTimeMillis()
            val hasAccess = withContext(ioDispatcher) { securityRepository.hasInstalledAppsAccess() }
            val elapsed = System.currentTimeMillis() - startedAt
            securityRepository.setInstalledAppsAccessFailed(!hasAccess)
            val showDialog =
                !hasAccess &&
                    !returningFromSettings &&
                    hadPreviousFailure &&
                    elapsed < INSTALLED_APPS_DIALOG_INTERACTION_THRESHOLD_MILLIS
            dispatch(VirusScanUpdate.AccessCheckFinished(showDialog))
            if (hasAccess) {
                if (mode == VirusScanMode.Deep) {
                    sendEvent(VirusScanEvent.RequestDeepScanPermission(mode))
                } else {
                    sendEvent(VirusScanEvent.NavigateToScan(mode))
                }
            }
        }
    }

    fun isScanNoticeAccepted(): Boolean = securityRepository.isScanNoticeAccepted()

    fun hasInstalledAppsAccessFailedBefore(): Boolean = securityRepository.hasInstalledAppsAccessFailedBefore()

    fun setInstalledAppsAccessFailed(failed: Boolean) = securityRepository.setInstalledAppsAccessFailed(failed)

    private fun startScan(mode: VirusScanMode) {
        resetScanState()
        val generation = scanGeneration
        completionJob = null

        handleScanStarted(mode, generation)
        refreshAdbRisk()
        startTerminalTimeout(mode, generation)

        scanEngine.startScan(mode, createScanListener(mode, generation))
    }

    private fun resetScanState() {
        scanGeneration++
        scanTerminalState = ScanTerminalState.Running
        completionJob?.cancel()
        completionJob = null
        stopTerminalTimeout()
        cancelAdbRiskCheck()
        display?.stop()
        display = null
        pendingThreatJobs.forEach { job -> job.cancel() }
        pendingThreatJobs.clear()
        threatIds.clear()
        scanEngine.cancelScan()
        scanStartedAt = 0L
        dispatch(VirusScanUpdate.ScanReset)
    }

    private fun refreshAdbRisk() {
        val generation = scanGeneration
        adbRiskJob?.cancel()
        adbRiskJob =
            viewModelScope.launch {
                val hasRisk =
                    withContext(defaultDispatcher) {
                        securityRepository.hasAdbRisk()
                    }
                if (generation == scanGeneration) dispatch(VirusScanUpdate.AdbRiskChecked(hasRisk))
            }
    }

    fun hasInstalledAppsAccess(): Boolean = securityRepository.hasInstalledAppsAccess()

    private fun cancelScan() {
        scanGeneration++
        completionJob?.cancel()
        completionJob = null
        stopTerminalTimeout()
        cancelAdbRiskCheck()
        display?.stop()
        display = null
        pendingThreatJobs.forEach { job -> job.cancel() }
        pendingThreatJobs.clear()
        scanEngine.cancelScan()
        scanStartedAt = 0L
    }

    private fun createScanListener(
        mode: VirusScanMode,
        generation: Int,
    ): VirusScanner.Listener {
        return object : VirusScanner.Listener {
            override fun onScanStarted() {
                viewModelScope.launch {
                    if (canAcceptSdkCallback(generation)) {
                        handleScanStarted(mode, generation)
                    }
                }
            }

            override fun onScanProgress(item: VirusScanItemModel?) {
                item ?: return
                viewModelScope.launch {
                    if (canAcceptSdkCallback(generation)) {
                        handleScanProgress(mode, item, generation)
                    }
                }
            }

            override fun onScanError(error: VirusScanError) {
                viewModelScope.launch {
                    if (!canAcceptSdkCallback(generation)) return@launch
                    handleSdkTerminal(mode, generation, ScanTerminalState.Failure)
                }
            }

            override fun onScanCanceled() {
                viewModelScope.launch {
                    handleSdkTerminal(mode, generation, ScanTerminalState.Failure)
                }
            }

            override fun onScanInterrupt() {
                viewModelScope.launch {
                    handleSdkTerminal(mode, generation, ScanTerminalState.Failure)
                }
            }

            override fun onScanFinished() {
                viewModelScope.launch {
                    handleSdkTerminal(mode, generation, ScanTerminalState.Success)
                }
            }
        }
    }

    private fun canAcceptSdkCallback(generation: Int): Boolean =
        generation == scanGeneration &&
            scanTerminalState == ScanTerminalState.Running &&
            !currentState.scanCompleted

    private fun handleScanStarted(
        mode: VirusScanMode,
        generation: Int,
    ) {
        if (generation != scanGeneration) return
        if (currentState.scanCompleted) return
        if (scanStartedAt == 0L) {
            scanStartedAt = System.currentTimeMillis()
        }
        dispatch(VirusScanUpdate.ScanStarted(mode))
        if (display == null) {
            display =
                VirusScanDisplay(
                    scope = viewModelScope,
                    mode = mode,
                    loadAppLabel = { withContext(ioDispatcher) { securityRepository.appLabel(it) } },
                    onUpdate = { if (generation == scanGeneration) dispatch(it) },
                ).also { it.start(scanStartedAt) }
        }
    }

    private fun handleScanProgress(
        mode: VirusScanMode,
        item: VirusScanItemModel,
        generation: Int,
    ) {
        val packageName = item.packageName?.takeIf { it.isNotBlank() }
        if (packageName != null) {
            if (item.isThreat) addThreat(item, isFile = false, generation = generation)
            display?.enqueueApp(packageName)
            return
        }

        if (mode == VirusScanMode.Deep) {
            val path = item.apkPath?.takeIf { it.isNotBlank() }
            if (item.isThreat) addThreat(item, isFile = true, generation = generation)
            if (path != null) display?.enqueuePath(path)
        }
    }

    private fun addThreat(
        item: VirusScanItemModel,
        isFile: Boolean,
        generation: Int,
    ) {
        val job =
            viewModelScope.launch {
                val threat =
                    withContext(ioDispatcher) {
                        securityRepository.threatFor(item, isFile)
                    }
                if (generation != scanGeneration) return@launch
                if (!threatIds.add(threat.id)) return@launch

                dispatch(VirusScanUpdate.ThreatFound(threat))
            }
        pendingThreatJobs.add(job)
        job.invokeOnCompletion { pendingThreatJobs.remove(job) }
    }

    private fun finishWhenUiIsReady(
        mode: VirusScanMode,
        generation: Int,
    ) {
        completionJob?.cancel()
        completionJob =
            viewModelScope.launch {
                val startedAt = scanStartedAt.takeIf { it > 0L } ?: System.currentTimeMillis()
                val elapsed = System.currentTimeMillis() - startedAt
                val remaining = (mode.minDurationMillis - elapsed).coerceAtLeast(0L)
                if (remaining > 0L) delay(remaining)
                pendingThreatJobs.toList().joinAll()
                adbRiskJob?.join()
                if (generation != scanGeneration) return@launch
                stopTerminalTimeout()
                display?.stop()
                display = null
                dispatch(VirusScanUpdate.ScanFinished)
                sendEvent(VirusScanEvent.NavigateToResult(currentState.effectiveThreatCount > 0))
            }
    }

    private fun startTerminalTimeout(
        mode: VirusScanMode,
        generation: Int,
    ) {
        terminalTimeoutJob?.cancel()
        val graceMillis =
            when (mode) {
                VirusScanMode.Quick -> QUICK_SCAN_CALLBACK_GRACE_MILLIS
                VirusScanMode.Deep -> DEEP_SCAN_CALLBACK_GRACE_MILLIS
            }
        terminalTimeoutJob =
            viewModelScope.launch {
                delay(mode.minDurationMillis + graceMillis)
                if (canAcceptSdkCallback(generation)) {
                    terminalTimeoutJob = null
                    handleSdkTerminal(mode, generation, ScanTerminalState.Failure)
                }
            }
    }

    private fun stopTerminalTimeout() {
        terminalTimeoutJob?.cancel()
        terminalTimeoutJob = null
    }

    private fun cancelAdbRiskCheck() {
        adbRiskJob?.cancel()
        adbRiskJob = null
    }

    private fun handleSdkTerminal(
        mode: VirusScanMode,
        generation: Int,
        terminalState: ScanTerminalState,
    ) {
        if (!canAcceptSdkCallback(generation)) return
        scanTerminalState = terminalState
        stopTerminalTimeout()
        finishWhenUiIsReady(mode, generation)
    }

    override fun onCleared() {
        cancelScan()
        super.onCleared()
    }
}
