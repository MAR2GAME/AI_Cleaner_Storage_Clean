package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.MemoryCleanResult
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.junkclean.error.JunkError
import com.aicleanerstorage.spaceclean.domain.junkclean.model.CleanupSummaryModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SCAN_PREVIEW_MIN_MILLIS = 1_000L
private const val CLEANING_ANIMATION_MIN_MILLIS = 1_600L

class JunkCleanViewModel(
    private val cleanRepository: CleanRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanAction, JunkCleanEvent>(
        initialState = JunkCleanUiState(),
    ) {
    private data class CleaningExecutionState(
        val pendingAuthorizationOutcomes: List<JunkDeleteOutcome> = emptyList(),
        val directCleanedFiles: List<JunkFile> = emptyList(),
        val directFailedFiles: List<JunkFile> = emptyList(),
        val directFreedSpace: Long = 0L,
        val memoryResult: MemoryCleanResult? = null,
    )

    private val FINISHING_ANIMATION_MILLIS = 800L

    private var progressJob: Job? = null
    private var scanJob: Job? = null

    @Volatile private var stopCleaning = false
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var cleaningExecutionState = CleaningExecutionState()

    private var permissionPrevious: JunkCleanUiState? = null
    private var pendingCleanFiles: List<JunkFile>? = null

    override fun onAction(intent: JunkCleanAction) {
        if (!viewModelScope.isActive) return
        if (exitEffectSent) return

        when (intent) {
            JunkCleanAction.Entered -> startScanIfNeeded()
            JunkCleanAction.ScanRequested -> startScan()
            JunkCleanAction.CleanClicked -> startCleaning()
            JunkCleanAction.BackClicked -> handleBack()
            is JunkCleanAction.PermissionResultReceived -> handlePermissionResult(intent)
            is JunkCleanAction.DeleteAuthorizationResultReceived -> handleAuthorizationResult(intent.deletedIds)
            JunkCleanAction.ContinueClicked -> {
                if (currentState.phase == JunkCleanPhase.Complete) {
                    requestExit(JunkExitTarget.HomeAfterComplete)
                }
            }
            JunkCleanAction.StopConfirmed -> {
                if (currentState.showStopDialog) {
                    val phase = currentState.stopDialogPhase
                    dismissStopDialog(resume = false)
                    if (phase == JunkCleanPhase.Cleaning) stopCleaning = true else requestExit()
                }
            }
            JunkCleanAction.StopDismissed -> {
                if (currentState.showStopDialog) dismissStopDialog(resume = true)
            }
            is JunkCleanAction.ReducerOnly -> reduce(intent)
        }
    }

    private fun handlePermissionResult(action: JunkCleanAction.PermissionResultReceived) {
        val previous = permissionPrevious
        permissionPrevious = null
        if (!action.granted) {
            dispatch(JunkPermissionDenied(action.request))
            requestExit()
            return
        }
        if (previous == null) return
        dispatch(JunkPermissionCheckRestored(action.request, previous))
        when (action.request) {
            JunkOperation.Scan -> runScan()
            JunkOperation.Clean -> {
                val files = pendingCleanFiles
                pendingCleanFiles = null
                if (files != null) runCleaning(files)
            }
        }
    }

    private fun handleBack() {
        if (currentState.showStopDialog) return
        when (currentState.phase) {
            JunkCleanPhase.Scanning, JunkCleanPhase.Cleaning -> requestStopDialog()
            JunkCleanPhase.CompleteAnimation, JunkCleanPhase.Complete ->
                requestExit(JunkExitTarget.HomeAfterComplete)
            else -> requestExit()
        }
    }

    private fun requestExit(target: JunkExitTarget = JunkExitTarget.Back) {
        if (exitEffectSent) return
        exitEffectSent = true
        scanJob?.cancel()
        cleaningJob?.cancel()
        progressJob?.cancel()
        sendEvent(JunkCleanEvent.Exit(target))
    }

    private fun reduce(action: JunkCleanAction) {
        setState { JunkCleanReducer.reduce(it, action) }
    }

    private fun startScanIfNeeded() {
        if (currentState.phase != JunkCleanPhase.Idle) return
        startScan()
    }

    private fun startScan() {
        if (exitEffectSent || cleaningJob?.isActive == true) return
        when (val phase = currentState.phase) {
            JunkCleanPhase.Idle, JunkCleanPhase.Preview -> Unit
            is JunkCleanPhase.Failed -> if (phase.operation != JunkOperation.Scan) return
            else -> return
        }
        if (scanJob?.isActive == true) return
        permissionPrevious = currentState
        dispatch(JunkPermissionCheckStarted(JunkOperation.Scan))
    }

    private fun runScan() {
        scanJob =
            viewModelScope.launch {
                try {
                    cleanRepository.clearSession()
                    val scanStartedAt = System.currentTimeMillis()
                    dispatch(JunkScanStarted)
                    observeProgress()
                    val result = withContext(ioDispatcher) { cleanRepository.performFullScan() }
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    dispatch(JunkScanCompleted(result))
                    delayRemainingScanAnimation(scanStartedAt)
                    loadPreview(result)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    dispatch(JunkFailed(JunkError.ScanFailed, duringScan = true))
                }
            }
    }

    private fun startCleaning() {
        if (exitEffectSent) return
        val state = currentState.takeIf { it.phase == JunkCleanPhase.Preview } ?: return
        val selectedFiles =
            state.groups
                .flatMap { it.items }
                .filter { it.isChecked }
                .map { it.junkFile }
        if (selectedFiles.isEmpty()) {
            dispatch(JunkCleaningRequested)
            return
        }
        if (cleaningJob?.isActive == true) return
        pendingCleanFiles = selectedFiles
        permissionPrevious = state
        dispatch(JunkPermissionCheckStarted(JunkOperation.Clean))
    }

    private fun runCleaning(files: List<JunkFile>) {
        stopCleaning = false
        cleaningJob =
            viewModelScope.launch {
                try {
                    dispatch(JunkCleaningRequested)
                    if (currentState.phase != JunkCleanPhase.Cleaning) return@launch

                    val cleaningStartedAt = System.currentTimeMillis()
                    val outcomes = withContext(ioDispatcher) { cleanRepository.deleteFileItems(files) { stopCleaning } }
                    currentCoroutineContext().ensureActive()
                    val deleted = outcomes.filter { it.deleted }
                    val pending = outcomes.filter { !it.deleted && it.authorizationId != null }
                    val failed = outcomes.filter { !it.deleted && it.authorizationId == null }
                    val memoryResult = if (stopCleaning) null else withContext(ioDispatcher) { cleanRepository.cleanMemory() }
                    currentCoroutineContext().ensureActive()

                    cleaningExecutionState =
                        CleaningExecutionState(
                            pendingAuthorizationOutcomes = pending,
                            directCleanedFiles = deleted.map { it.junkFile },
                            directFailedFiles = failed.map { it.junkFile },
                            directFreedSpace = deleted.sumOf { it.freedBytes },
                            memoryResult = memoryResult,
                        )

                    if (stopCleaning) {
                        publishStoppedPreview(deleted.map { it.junkFile }, deleted.sumOf { it.freedBytes })
                        return@launch
                    }
                    val ids = pending.mapNotNull { it.authorizationId }.distinct()
                    if (ids.isNotEmpty()) {
                        dispatch(JunkDeleteAuthorizationRequested(ids.size))
                        sendEvent(JunkCleanEvent.RequestDeleteAuthorization(ids))
                    } else {
                        delayRemainingCleaningAnimation(cleaningStartedAt)
                        finishCleaning(
                            extraCleanedFiles = emptyList(),
                            extraFailedFiles = pending.map { it.junkFile },
                            extraFreedSpace = 0L,
                        )
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    dispatch(JunkFailed(JunkError.CleanFailed))
                }
            }
    }

    private fun handleAuthorizationResult(deletedIds: Set<String>) {
        if (currentState.phase != JunkCleanPhase.AwaitingAuthorization) return
        val pending = cleaningExecutionState.pendingAuthorizationOutcomes
        if (pending.isEmpty()) return

        dispatch(JunkDeleteAuthorizationHandled)
        val cleaningStartedAt = System.currentTimeMillis()
        cleaningJob?.cancel()
        cleaningJob =
            viewModelScope.launch {
                try {
                    val (deleted, failed) = pending.partition { it.authorizationId in deletedIds }
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    finishCleaning(
                        extraCleanedFiles = deleted.map { it.junkFile },
                        extraFailedFiles = failed.map { it.junkFile },
                        extraFreedSpace = deleted.sumOf { it.junkFile.fileSize },
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    dispatch(JunkFailed(JunkError.CleanFailedAfterAuthorization))
                }
            }
    }

    private fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        progressJob?.cancel()
        progressJob = null
        cleaningJob?.cancel()
        cleaningJob = null
        dispatch(JunkActiveOperationCancelled)
        cleaningExecutionState = CleaningExecutionState()
    }

    private fun publishStoppedPreview(
        deleted: List<JunkFile>,
        bytes: Long,
    ) {
        val files = cleanRepository.scanResult.value ?: return
        val selected =
            currentState.groups
                .flatMap { it.items }
                .filter { it.isChecked }
                .map { it.junkFile }
                .toSet()
        val summary = CleanupSummaryModel(freedSpace = bytes, cleanedCount = deleted.size)
        dispatch(JunkCleaningStopped(files, selected, summary))
        cleaningExecutionState = CleaningExecutionState()
    }

    private fun cancelCleaningAndReturnToPreview() {
        cleaningJob?.cancel()
        cleaningJob = null
        cleaningExecutionState = CleaningExecutionState()
        dispatch(JunkCleaningCancelled)
    }

    private fun requestStopDialog() {
        val phase = currentState.phase
        when (phase) {
            JunkCleanPhase.Scanning -> cancelActiveOperation()
            JunkCleanPhase.Cleaning -> Unit
            JunkCleanPhase.AwaitingAuthorization,
            -> cancelCleaningAndReturnToPreview()
            else -> return
        }
        dispatch(JunkStopDialogShown(phase))
    }

    private fun dismissStopDialog(resume: Boolean) {
        val stoppedPhase = currentState.stopDialogPhase
        dispatch(JunkStopDialogDismissed)
        if (resume && stoppedPhase == JunkCleanPhase.Scanning) {
            startScanIfNeeded()
        }
    }

    private fun observeProgress() {
        progressJob?.cancel()
        progressJob =
            viewModelScope.launch {
                cleanRepository.scanProgress.collect { progress ->
                    if (currentState.phase != JunkCleanPhase.Scanning) return@collect
                    val currentResult = cleanRepository.scanResult.value
                    val foundSize = currentResult?.totalSize ?: progress.foundSize
                    reduce(
                        JunkScanProgressUpdated(
                            progress = progress.percent,
                            currentCategory = progress.currentCategory,
                            foundItemCount = currentResult?.totalCount ?: progress.foundCount,
                            foundTotalSize = foundSize,
                        ),
                    )
                }
            }
    }

    private fun loadPreview(scanResult: ScanResult?) {
        if (scanResult == null) {
            dispatch(JunkPreviewLoadFailed)
            return
        }

        dispatch(JunkPreviewLoaded(scanResult))
    }

    private suspend fun finishCleaning(
        extraCleanedFiles: List<JunkFile>,
        extraFailedFiles: List<JunkFile>,
        extraFreedSpace: Long,
    ) {
        currentCoroutineContext().ensureActive()
        val cleanedFiles = cleaningExecutionState.directCleanedFiles + extraCleanedFiles
        if (stopCleaning) {
            publishStoppedPreview(cleanedFiles, cleaningExecutionState.directFreedSpace + extraFreedSpace)
            return
        }
        val failedFiles = cleaningExecutionState.directFailedFiles + extraFailedFiles
        val freedSpace = cleaningExecutionState.directFreedSpace + extraFreedSpace
        val memoryResult = cleaningExecutionState.memoryResult
        val summary =
            CleanupSummaryModel(
                freedSpace = freedSpace,
                cleanedCount = cleanedFiles.size,
                failedCount = failedFiles.size,
                memoryFreedBytes = memoryResult?.freedBytes ?: 0L,
            )

        cleanRepository.removeCleanedFiles(cleanedFiles)

        cleaningExecutionState = CleaningExecutionState()
        dispatch(JunkCleaningCompleted(summary))
        delay(FINISHING_ANIMATION_MILLIS)
        if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
            dispatch(JunkResultAnimationCompleted)
        }
    }

    private suspend fun delayRemainingScanAnimation(startedAtMillis: Long) {
        val remainingMillis = scanPreviewMinMillis - (System.currentTimeMillis() - startedAtMillis)
        delay(remainingMillis.coerceAtLeast(150L))
    }

    private suspend fun delayRemainingCleaningAnimation(startedAtMillis: Long) {
        val remainingMillis = cleaningAnimationMinMillis - (System.currentTimeMillis() - startedAtMillis)
        if (remainingMillis > 0L) delay(remainingMillis)
    }

    override fun onCleared() {
        scanJob?.cancel()
        progressJob?.cancel()
        cleaningJob?.cancel()
        super.onCleared()
    }
}
