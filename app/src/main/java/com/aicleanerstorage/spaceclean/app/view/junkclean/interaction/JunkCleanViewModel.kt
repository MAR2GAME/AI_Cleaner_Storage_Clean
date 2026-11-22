package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.error.JunkError
import com.aicleanerstorage.spaceclean.domain.model.junk.CleanupSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
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
    private val cleanRepository: JunkCleanRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanAction, JunkCleanEvent>(
        initialState = JunkCleanUiState(),
    ) {
    private data class CleaningExecutionState(
        val directCleanedFiles: List<JunkFileModel> = emptyList(),
        val directFailedFiles: List<JunkFileModel> = emptyList(),
        val directFreedSpace: Long = 0L,
        val memoryResult: JunkMemoryCleanModel? = null,
    )

    private val finishingAnimationMillis = 800L

    private var scanJob: Job? = null

    @Volatile private var stopCleaning = false
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var cleaningExecutionState = CleaningExecutionState()
    private var lastScanResult: JunkScanResultModel? = null

    private var permissionPrevious: JunkCleanUiState? = null
    private var pendingCleanFiles: List<JunkFileModel>? = null

    override fun onAction(intent: JunkCleanAction) {
        if (!viewModelScope.isActive) return
        if (exitEffectSent) return

        when (intent) {
            JunkCleanAction.Entered -> startScanIfNeeded()
            JunkCleanAction.ScanRequested -> startScan()
            JunkCleanAction.CleanClicked -> startCleaning()
            JunkCleanAction.BackClicked -> handleBack()
            is JunkCleanAction.PermissionResultReceived -> handlePermissionResult(intent)
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
            is JunkCleanAction.ItemToggled, is JunkCleanAction.CategoriesToggled -> reduce(intent)
        }
    }

    private fun handlePermissionResult(action: JunkCleanAction.PermissionResultReceived) {
        val previous = permissionPrevious
        permissionPrevious = null
        if (!action.granted) {
            update(JunkPermissionDenied(action.request))
            requestExit()
            return
        }
        if (previous == null) return
        update(JunkPermissionCheckRestored(action.request, previous))
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
        sendEvent(JunkCleanEvent.Exit(target))
    }

    private fun reduce(action: JunkCleanAction) {
        setState { JunkCleanReducer.reduce(it, action) }
    }

    private fun update(update: JunkCleanUpdate) {
        setState { JunkCleanReducer.reduce(it, update) }
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
        update(JunkPermissionCheckStarted(JunkOperation.Scan))
    }

    private fun runScan() {
        scanJob =
            viewModelScope.launch {
                try {
                    val scanStartedAt = System.currentTimeMillis()
                    update(JunkScanStarted)
                    val result =
                        withContext(ioDispatcher) {
                            cleanRepository.scan { progress ->
                                if (currentState.phase == JunkCleanPhase.Scanning) {
                                    update(
                                        JunkScanProgressUpdated(
                                            progress = progress.percent,
                                            currentCategory = progress.currentCategory,
                                            foundItemCount = progress.foundCount,
                                            foundTotalSize = progress.foundSize,
                                        ),
                                    )
                                }
                            }
                        }.getOrElse {
                            update(JunkFailed(JunkError.ScanFailed, duringScan = true))
                            return@launch
                        }
                    currentCoroutineContext().ensureActive()
                    lastScanResult = result
                    update(JunkScanCompleted(result))
                    delayRemainingScanAnimation(scanStartedAt)
                    loadPreview(result)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    update(JunkFailed(JunkError.ScanFailed, duringScan = true))
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
                .map { it.file }
        if (selectedFiles.isEmpty()) {
            update(JunkCleaningRequested)
            return
        }
        if (cleaningJob?.isActive == true) return
        pendingCleanFiles = selectedFiles
        permissionPrevious = state
        update(JunkPermissionCheckStarted(JunkOperation.Clean))
    }

    private fun runCleaning(files: List<JunkFileModel>) {
        stopCleaning = false
        cleaningJob =
            viewModelScope.launch {
                try {
                    update(JunkCleaningRequested)
                    if (currentState.phase != JunkCleanPhase.Cleaning) return@launch

                    val cleaningStartedAt = System.currentTimeMillis()
                    val execution = executeCleaning(files) ?: return@launch
                    cleaningExecutionState = execution
                    if (stopCleaning) {
                        publishStoppedPreview(
                            execution.directCleanedFiles,
                            execution.directFreedSpace,
                        )
                        return@launch
                    }
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    finishCleaning(
                        extraCleanedFiles = emptyList(),
                        extraFailedFiles = emptyList(),
                        extraFreedSpace = 0L,
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    update(JunkFailed(JunkError.CleanFailed))
                }
            }
    }

    private suspend fun executeCleaning(files: List<JunkFileModel>): CleaningExecutionState? {
        val outcomes =
            withContext(ioDispatcher) { cleanRepository.deleteFiles(files) { stopCleaning } }.getOrElse {
                update(JunkFailed(JunkError.CleanFailed))
                return null
            }
        currentCoroutineContext().ensureActive()
        val deleted = outcomes.filter { it.deleted }
        val failed = outcomes.filter { !it.deleted }
        val memoryResult =
            if (stopCleaning) {
                null
            } else {
                withContext(ioDispatcher) { cleanRepository.cleanMemory() }.getOrNull()
            }
        currentCoroutineContext().ensureActive()
        return CleaningExecutionState(
            directCleanedFiles = deleted.map { it.file },
            directFailedFiles = failed.map { it.file },
            directFreedSpace = deleted.sumOf { it.freedBytes },
            memoryResult = memoryResult,
        )
    }

    private fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        cleaningJob?.cancel()
        cleaningJob = null
        update(JunkActiveOperationCancelled)
        cleaningExecutionState = CleaningExecutionState()
    }

    private fun publishStoppedPreview(
        deleted: List<JunkFileModel>,
        bytes: Long,
    ) {
        val files = lastScanResult ?: return
        val selected =
            currentState.groups
                .flatMap { it.items }
                .filter { it.isChecked }
                .map { it.file }
                .toSet()
        val summary = CleanupSummaryModel(freedSpace = bytes, cleanedCount = deleted.size)
        update(JunkCleaningStopped(files, selected, summary))
        cleaningExecutionState = CleaningExecutionState()
    }

    private fun requestStopDialog() {
        val phase = currentState.phase
        when (phase) {
            JunkCleanPhase.Scanning -> cancelActiveOperation()
            JunkCleanPhase.Cleaning -> Unit
            else -> return
        }
        update(JunkStopDialogShown(phase))
    }

    private fun dismissStopDialog(resume: Boolean) {
        val stoppedPhase = currentState.stopDialogPhase
        update(JunkStopDialogDismissed)
        if (resume && stoppedPhase == JunkCleanPhase.Scanning) {
            startScanIfNeeded()
        }
    }

    private fun loadPreview(scanResult: JunkScanResultModel?) {
        if (scanResult == null) {
            update(JunkPreviewLoadFailed)
            return
        }

        update(JunkPreviewLoaded(scanResult))
    }

    private suspend fun finishCleaning(
        extraCleanedFiles: List<JunkFileModel>,
        extraFailedFiles: List<JunkFileModel>,
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

        lastScanResult =
            lastScanResult?.let { result ->
                result.copy(files = result.files.filterNot { it in cleanedFiles })
            }

        cleaningExecutionState = CleaningExecutionState()
        update(JunkCleaningCompleted(summary))
        delay(finishingAnimationMillis)
        if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
            update(JunkResultAnimationCompleted)
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
        cleaningJob?.cancel()
        super.onCleared()
    }
}
