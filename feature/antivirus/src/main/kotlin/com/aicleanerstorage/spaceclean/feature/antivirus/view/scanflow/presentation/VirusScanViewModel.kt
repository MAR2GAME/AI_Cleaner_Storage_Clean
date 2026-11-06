package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation

import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanEngine
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanEngineListener
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanErrorKind
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanItem
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.AntivirusPreferences
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.TrustlookConfigurationException
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction

private const val TAG = "VirusScan"
private const val PROGRESS_TICK_MILLIS = 40L
private const val WAITING_FOR_SDK_PROGRESS = 0.99f
private const val QUICK_SCAN_CALLBACK_GRACE_MILLIS = 5_000L
private const val DEEP_SCAN_CALLBACK_GRACE_MILLIS = 10_000L
private const val INSTALLED_APPS_DIALOG_INTERACTION_THRESHOLD_MILLIS = 1_500L

private enum class ScanTerminalState {
    Running,
    Success,
    Failure,
}

class VirusScanViewModel constructor(

    private val scanEngine: VirusScanEngine,
    private val securityRepository: VirusSecurityRepository,
    private val preferences: AntivirusPreferences,
    private val permissions: PermissionGateway? = null,
) : MviViewModel<VirusScanUiState, VirusScanIntent, VirusScanEffect>(VirusScanUiState()) {

    private var resultVisible = false
    private var emptyResultSent = false
    private fun checkEmptyResult() {
        if (resultVisible && currentState.effectiveThreatCount == 0 && !emptyResultSent) {
            emptyResultSent = true
            emitEffect(VirusScanEffect.NavigateToResult(false))
        }
    }
    private val stateUpdateLock = Any()
    private fun changeState(transform: (VirusScanUiState) -> VirusScanUiState) {
        synchronized(stateUpdateLock) { dispatch(VirusStateChanged(transform(currentState))) }
        checkEmptyResult()
    }
    private fun changeHome(transform: (VirusHomeUiState) -> VirusHomeUiState) {
        changeState { it.copy(home = transform(it.home)) }
    }
    private fun publishState(value: VirusScanUiState) = changeState { value.copy(home = it.home) }

    private var completionJob: Job? = null
    private var progressJob: Job? = null
    private var terminalTimeoutJob: Job? = null
    private var adbRiskJob: Job? = null
    private var appDisplayJob: Job? = null
    private var pathDisplayJob: Job? = null
    private var appDisplayChannel = Channel<String>(Channel.UNLIMITED)
    private var pathDisplayChannel = Channel<String>(Channel.UNLIMITED)
    private var scanStartedAt: Long = 0L
    private var scanGeneration = 0
    private var scanTerminalState = ScanTerminalState.Running
    private var isAppDisplayStarted = false
    private var isAppDisplayStopped = false
    private var isPathDisplayStarted = false
    private val processedPackageNames = mutableSetOf<String>()
    private val threatIds = mutableSetOf<String>()
    private val pendingThreatJobs = ConcurrentHashMap.newKeySet<Job>()

    override fun onIntent(action: VirusScanIntent) {
        when (action) {
            VirusScanIntent.ResultBack -> emitEffect(VirusScanEffect.Back)
            VirusScanIntent.ResultEntered -> { resultVisible = true; emptyResultSent = false; checkEmptyResult() }
            VirusScanIntent.ResultLeft -> resultVisible = false
            VirusScanIntent.SolveAdbRisk -> emitEffect(VirusScanEffect.OpenDeveloperSettings)
            is VirusScanIntent.SolveAppThreat -> emitEffect(VirusScanEffect.OpenAppSettings(action.packageName))
            is VirusScanIntent.DeleteFileThreat -> emitEffect(VirusScanEffect.DeleteFile(action.path))
            is VirusScanIntent.Navigate -> emitEffect(VirusScanEffect.Navigate(action.destination))
            VirusScanIntent.ResetScanState -> resetScanState()
            VirusScanIntent.RefreshAdbRisk -> refreshAdbRisk()
            VirusScanIntent.ClearError -> clearError()
            is VirusScanIntent.RemoveThreatByPackage -> removeThreatByPackage(action.packageName)
            is VirusScanIntent.RemoveThreatByFilePath -> removeThreatByFilePath(action.path)
            else -> {
        when (action) {
            is VirusStateChanged -> updateState { VirusScanReducer.reduce(this, action) }
            is VirusScanIntent.StartScan -> startScan(action.mode)
            VirusScanIntent.CancelScan -> cancelScan()
            VirusScanIntent.Entered -> {
                if (!preferences.isScanNoticeAccepted()) {
                    changeHome { it.copy(showScanNotice = true) }
                }
            }
            VirusScanIntent.Back -> emitEffect(VirusScanEffect.Exit)
            is VirusScanIntent.ScanRequested -> requestHomeScan(action.mode)
            VirusScanIntent.ScanNoticeAccepted -> {
                preferences.setScanNoticeAccepted()
                val mode = currentState.home.pendingScanMode
                changeHome { it.copy(showScanNotice = false) }
                if (mode != null) checkInstalledAppsAccess(mode, returningFromSettings = false)
            }
            VirusScanIntent.ScanNoticeDeclined -> {
                changeHome { it.copy(showScanNotice = false) }
                emitEffect(VirusScanEffect.Exit)
            }
            VirusScanIntent.InstalledAppsSettingsRequested -> {
                changeHome {
                    it.copy(
                        showInstalledAppsPermissionDialog = false,
                        waitingForSettingsReturn = true,
                    )
                }
                emitEffect(VirusScanEffect.OpenInstalledAppsSettings)
            }
            VirusScanIntent.InstalledAppsDialogDismissed -> {
                changeHome {
                    it.copy(
                        showInstalledAppsPermissionDialog = false,
                        scanRequestPending = false,
                    )
                }
            }
            VirusScanIntent.Resumed -> {
                val state = currentState.home
                val mode = state.pendingScanMode
                if (state.waitingForSettingsReturn && mode != null) {
                    changeHome { it.copy(waitingForSettingsReturn = false) }
                    checkInstalledAppsAccess(mode, returningFromSettings = true)
                }
            }
            VirusScanIntent.SettingsLaunchFailed -> {
                changeHome {
                    it.copy(
                        waitingForSettingsReturn = false,
                        scanRequestPending = false,
                        showInstalledAppsPermissionDialog = true,
                    )
                }
            }
            is VirusScanIntent.DeepScanPermissionResult -> {
                val mode = currentState.home.pendingScanMode ?: VirusScanMode.Deep
                changeHome { it.copy(scanRequestPending = false) }
                if (action.granted) {
                    emitEffect(VirusScanEffect.NavigateToScan(mode))
                }
            }
            else -> Unit
        }
    
            }
        }
    }

    private fun requestHomeScan(mode: VirusScanMode) {
        if (currentState.home.scanRequestPending) return
        changeHome { it.copy(pendingScanMode = mode) }
        if (!preferences.isScanNoticeAccepted()) {
            changeHome { it.copy(showScanNotice = true) }
            return
        }
        checkInstalledAppsAccess(mode, returningFromSettings = false)
    }

    private fun checkInstalledAppsAccess(
        mode: VirusScanMode,
        returningFromSettings: Boolean,
    ) {
        if (currentState.home.scanRequestPending) return
        changeHome { it.copy(scanRequestPending = true, pendingScanMode = mode) }
        viewModelScope.launch {
            val hadPreviousFailure = preferences.hasInstalledAppsAccessFailedBefore()
            val startedAt = System.currentTimeMillis()
            val hasAccess = withContext(Dispatchers.IO) { securityRepository.hasInstalledAppsAccess() }
            val elapsed = System.currentTimeMillis() - startedAt
            if (hasAccess) {
                preferences.setInstalledAppsAccessFailed(false)
                changeHome {
                    it.copy(
                        showInstalledAppsPermissionDialog = false,
                        waitingForSettingsReturn = false,
                    )
                }
                if (mode == VirusScanMode.Deep) {
                    when (permissions?.let { ActionPermissionGate(it).checkAction(ProtectedAction.VirusDeepScanStart) } ?: GateResult.Allow) {
                        GateResult.Allow -> {
                            changeHome { it.copy(scanRequestPending = false) }
                            emitEffect(VirusScanEffect.NavigateToScan(mode))
                        }
                        is GateResult.Deny, GateResult.Busy, GateResult.Cancelled -> {
                            changeHome { it.copy(scanRequestPending = false) }
                        }
                    }
                } else {
                    changeHome { it.copy(scanRequestPending = false) }
                    emitEffect(VirusScanEffect.NavigateToScan(mode))
                }
            } else {
                preferences.setInstalledAppsAccessFailed(true)
                changeHome {
                    it.copy(
                        scanRequestPending = false,
                        waitingForSettingsReturn = false,
                        showInstalledAppsPermissionDialog =
                            !returningFromSettings && hadPreviousFailure &&
                                elapsed < INSTALLED_APPS_DIALOG_INTERACTION_THRESHOLD_MILLIS,
                    )
                }
            }
        }
    }

    fun isScanNoticeAccepted(): Boolean = preferences.isScanNoticeAccepted()

    fun acceptScanNotice() = preferences.setScanNoticeAccepted()

    fun hasInstalledAppsAccessFailedBefore(): Boolean = preferences.hasInstalledAppsAccessFailedBefore()

    fun setInstalledAppsAccessFailed(failed: Boolean) = preferences.setInstalledAppsAccessFailed(failed)

    private fun startScan(mode: VirusScanMode) {
        resetScanState()
        val generation = scanGeneration
        completionJob = null

        publishState(VirusScanUiState(
            mode = mode,
            isScanning = false,
            currentIcon = "@protection"
        ))

        handleScanStarted(mode, generation)
        refreshAdbRisk()
        startTerminalTimeout(mode, generation)

        if (!securityRepository.trustlookApiKeyState().isConfigured) {
            handleStartFailure(mode, generation, TrustlookConfigurationException())
            return
        }

        runCatching {
            val listener = createScanListener(mode, generation)
            scanEngine.startScan(mode, listener)
        }.onFailure { error ->
            handleStartFailure(mode, generation, error)
        }
    }

    private fun resetScanState() {
        scanGeneration++
        scanTerminalState = ScanTerminalState.Running
        completionJob?.cancel()
        completionJob = null
        stopTerminalTimeout()
        cancelAdbRiskCheck()
        stopTrackProgress()
        resetDisplayQueues()
        pendingThreatJobs.forEach { job -> job.cancel() }
        pendingThreatJobs.clear()
        processedPackageNames.clear()
        threatIds.clear()
        scanEngine.cancelScan()
        scanStartedAt = 0L
        publishState(VirusScanUiState())
    }

    private fun refreshAdbRisk() {
        val generation = scanGeneration
        adbRiskJob?.cancel()
        adbRiskJob = viewModelScope.launch {
            val hasRisk = withContext(Dispatchers.Default) {
                securityRepository.hasAdbRisk()
            }
            changeState { state ->
                if (generation == scanGeneration) {
                    state.copy(hasAdbRisk = hasRisk)
                } else {
                    state
                }
            }
        }
    }

    fun hasInstalledAppsAccess(): Boolean = securityRepository.hasInstalledAppsAccess()

    private fun clearError() {
        changeState { state -> state.copy(errorMessage = null) }
    }

    private fun cancelScan() {
        scanGeneration++
        completionJob?.cancel()
        completionJob = null
        stopTerminalTimeout()
        cancelAdbRiskCheck()
        stopTrackProgress()
        stopDisplayQueues()
        pendingThreatJobs.forEach { job -> job.cancel() }
        pendingThreatJobs.clear()
        scanEngine.cancelScan()
        scanStartedAt = 0L
        changeState { state ->
            state.copy(isScanning = false, scanCompleted = false)
        }
    }

    private fun removeThreatByPackage(packageName: String) {
        changeState { state ->
            val nextThreats = state.threats.filterNot { it.packageName == packageName }
            state.copy(
                threats = nextThreats,
                appThreatCount = nextThreats.count { !it.isFile },
                fileThreatCount = nextThreats.count { it.isFile }
            )
        }
        threatIds.removeAll { it == packageName || it.endsWith(":$packageName") }
    }

    private fun removeThreatByFilePath(path: String) {
        changeState { state ->
            val nextThreats = state.threats.filterNot { it.apkPath == path }
            state.copy(
                threats = nextThreats,
                appThreatCount = nextThreats.count { !it.isFile },
                fileThreatCount = nextThreats.count { it.isFile }
            )
        }
        threatIds.remove("file:$path")
    }

    private fun createScanListener(mode: VirusScanMode, generation: Int): VirusScanEngineListener {
        return object : VirusScanEngineListener {
            override fun onScanStarted() {
                viewModelScope.launch {
                    if (canAcceptSdkCallback(generation)) {
                        handleScanStarted(mode, generation)
                    }
                }
            }

            override fun onScanProgress(item: VirusScanItem?) {
                item ?: return
                viewModelScope.launch {
                    if (canAcceptSdkCallback(generation)) {
                        handleScanProgress(mode, item, generation)
                    }
                }
            }

            override fun onScanError(
                code: Int,
                message: String?,
                kind: VirusScanErrorKind,
            ) {
                viewModelScope.launch {
                    if (!canAcceptSdkCallback(generation)) return@launch
                    securityRepository.reportScanError(code, message)
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

    private fun canAcceptSdkCallback(generation: Int): Boolean {
        return generation == scanGeneration &&
            scanTerminalState == ScanTerminalState.Running &&
            !currentState.scanCompleted
    }

    private fun handleScanStarted(mode: VirusScanMode, generation: Int) {
        if (generation != scanGeneration) return
        if (currentState.scanCompleted) return
        if (scanStartedAt == 0L) {
            scanStartedAt = System.currentTimeMillis()
        }
        changeState { state ->
            if (generation == scanGeneration && !state.scanCompleted) {
                state.copy(
                    mode = mode,
                    isScanning = true,
                    currentIcon = state.currentIcon ?: "@protection"
                )
            } else {
                state
            }
        }
        startTrackProgress(mode, generation)
    }

    private fun handleScanProgress(mode: VirusScanMode, item: VirusScanItem, generation: Int) {
        val packageName = item.packageName?.takeIf { it.isNotBlank() }
        if (packageName != null) {
            if (item.score >= 6) addThreat(item, isFile = false, generation = generation)
            appDisplayChannel.trySend(packageName)
            return
        }

        if (mode == VirusScanMode.Deep) {
            val path = item.apkPath?.takeIf { it.isNotBlank() }
            if (item.score >= 6) addThreat(item, isFile = true, generation = generation)
            if (path != null) pathDisplayChannel.trySend(path)
        }
    }

    private fun addThreat(item: VirusScanItem, isFile: Boolean, generation: Int) {
        val job = viewModelScope.launch {
            val threat = withContext(Dispatchers.IO) {
                securityRepository.threatFor(item, isFile)
            }
            if (generation != scanGeneration) return@launch
            if (!threatIds.add(threat.id)) return@launch

            changeState { state ->
                if (generation != scanGeneration) return@changeState state
                val nextThreats = state.threats + threat
                state.copy(
                    threats = nextThreats,
                    appThreatCount = nextThreats.count { !it.isFile },
                    fileThreatCount = nextThreats.count { it.isFile }
                )
            }
        }
        pendingThreatJobs.add(job)
        job.invokeOnCompletion { pendingThreatJobs.remove(job) }
    }

    private fun finishWhenUiIsReady(mode: VirusScanMode, generation: Int) {
        completionJob?.cancel()
        completionJob = viewModelScope.launch {
            val startedAt = scanStartedAt.takeIf { it > 0L } ?: System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startedAt
            val remaining = (mode.minDurationMillis - elapsed).coerceAtLeast(0L)
            if (remaining > 0L) delay(remaining)
            pendingThreatJobs.toList().joinAll()
            adbRiskJob?.join()
            if (generation != scanGeneration) return@launch
            stopTrackProgress()
            stopTerminalTimeout()
            stopDisplayQueues()
            changeState { state ->
                state.copy(
                    isScanning = false,
                    scanCompleted = true,
                    progressFraction = 1f
                )
            }
            emitEffect(VirusScanEffect.NavigateToResult(currentState.effectiveThreatCount > 0))
        }
    }

    private fun startTrackProgress(mode: VirusScanMode, generation: Int) {
        if (progressJob?.isActive == true) return
        progressJob = viewModelScope.launch {
            while (true) {
                if (generation != scanGeneration) break
                val elapsed = System.currentTimeMillis() - scanStartedAt
                val fraction = (
                    elapsed.toFloat() / mode.minDurationMillis * WAITING_FOR_SDK_PROGRESS
                ).coerceIn(0f, WAITING_FOR_SDK_PROGRESS)
                changeState { state ->
                    if (state.isScanning) {
                        state.copy(progressFraction = maxOf(state.progressFraction, fraction))
                    } else {
                        state
                    }
                }
                handleProgressStageTriggers(mode, fraction, generation)
                if (fraction >= WAITING_FOR_SDK_PROGRESS) break
                delay(PROGRESS_TICK_MILLIS)
            }
        }
    }

    private fun stopTrackProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun startTerminalTimeout(mode: VirusScanMode, generation: Int) {
        terminalTimeoutJob?.cancel()
        val graceMillis = when (mode) {
            VirusScanMode.Quick -> QUICK_SCAN_CALLBACK_GRACE_MILLIS
            VirusScanMode.Deep -> DEEP_SCAN_CALLBACK_GRACE_MILLIS
        }
        terminalTimeoutJob = viewModelScope.launch {
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

    private fun handleProgressStageTriggers(mode: VirusScanMode, progress: Float, generation: Int) {
        if (progress >= mode.circleStartThreshold(circleIndex = 1)) {
            startAppDisplayConsumer(mode, generation)
        }
        if (mode == VirusScanMode.Quick && progress >= mode.circleFillThreshold(circleIndex = 1)) {
            stopAppDisplayConsumer()
        }
        if (mode == VirusScanMode.Deep && progress >= mode.circleStartThreshold(circleIndex = 2)) {
            stopAppDisplayConsumer()
            enterPathDisplayStage(generation)
            startPathDisplayConsumer(mode, generation)
        }
    }

    private fun handleStartFailure(
        mode: VirusScanMode,
        generation: Int,
        error: Throwable,
    ) {
        if (generation != scanGeneration) return
        securityRepository.reportStartError(error)
        handleSdkTerminal(mode, generation, ScanTerminalState.Failure)
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

    private fun startAppDisplayConsumer(mode: VirusScanMode, generation: Int) {
        if (isAppDisplayStarted || isAppDisplayStopped || generation != scanGeneration) return
        isAppDisplayStarted = true
        appDisplayJob = viewModelScope.launch(Dispatchers.Default) {
            var lastUpdateTime = 0L
            for (packageName in appDisplayChannel) {
                if (generation != scanGeneration) break
                if (!processedPackageNames.add(packageName)) continue

                val appLabelAndIcon = withContext(Dispatchers.IO) {
                    securityRepository.appLabel(packageName) to packageName
                }
                delayUntilNextDisplay(lastUpdateTime, mode.displayUpdateIntervalMillis)
                if (generation != scanGeneration) break

                changeState { state ->
                    if (state.isScanning && !state.isPathMode) {
                        state.copy(
                            currentLabel = appLabelAndIcon.first,
                            currentIcon = appLabelAndIcon.second
                        )
                    } else {
                        state
                    }
                }
                lastUpdateTime = System.currentTimeMillis()
            }
        }
    }

    private fun stopAppDisplayConsumer() {
        if (isAppDisplayStopped) return
        isAppDisplayStopped = true
        appDisplayJob?.cancel()
        appDisplayJob = null
        appDisplayChannel.close()
    }

    private fun enterPathDisplayStage(generation: Int) {
        if (isPathDisplayStarted || generation != scanGeneration) return
        changeState { state ->
            if (state.isScanning) {
                state.copy(
                    isPathMode = true,
                    currentLabel = "",
                    currentIcon = "@protection"
                )
            } else {
                state
            }
        }
    }

    private fun startPathDisplayConsumer(mode: VirusScanMode, generation: Int) {
        if (isPathDisplayStarted || generation != scanGeneration) return
        isPathDisplayStarted = true
        pathDisplayJob = viewModelScope.launch(Dispatchers.Default) {
            var lastUpdateTime = 0L
            for (path in pathDisplayChannel) {
                if (generation != scanGeneration) break
                delayUntilNextDisplay(lastUpdateTime, mode.displayUpdateIntervalMillis)
                if (generation != scanGeneration) break

                changeState { state ->
                    if (state.isScanning && state.isPathMode) {
                        state.copy(currentLabel = path)
                    } else {
                        state
                    }
                }
                lastUpdateTime = System.currentTimeMillis()
            }
        }
    }

    private suspend fun delayUntilNextDisplay(lastUpdateTime: Long, intervalMillis: Long) {
        if (lastUpdateTime == 0L) return
        val elapsed = System.currentTimeMillis() - lastUpdateTime
        if (elapsed < intervalMillis) delay(intervalMillis - elapsed)
    }

    private fun resetDisplayQueues() {
        stopDisplayQueues()
        appDisplayChannel.cancel()
        pathDisplayChannel.cancel()
        appDisplayChannel = Channel(Channel.UNLIMITED)
        pathDisplayChannel = Channel(Channel.UNLIMITED)
        isAppDisplayStarted = false
        isAppDisplayStopped = false
        isPathDisplayStarted = false
    }

    private fun stopDisplayQueues() {
        appDisplayJob?.cancel()
        pathDisplayJob?.cancel()
        appDisplayJob = null
        pathDisplayJob = null
        closeDisplayQueues()
    }

    private fun closeDisplayQueues() {
        appDisplayChannel.close()
        pathDisplayChannel.close()
    }

    override fun onCleared() {
        cancelScan()
        super.onCleared()
    }
}
