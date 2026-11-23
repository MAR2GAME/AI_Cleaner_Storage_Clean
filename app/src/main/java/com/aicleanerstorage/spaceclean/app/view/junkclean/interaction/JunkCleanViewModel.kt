package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.error.JunkError
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.CleanupSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
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
import java.util.concurrent.atomic.AtomicBoolean

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
    private val finishingAnimationMillis = 800L

    private var scanJob: Job? = null

    private val stopCleaning = AtomicBoolean(false)
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var permissionPrevious: JunkCleanUiState? = null

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
                    requestExit()
                }
            }
            JunkCleanAction.StopConfirmed -> {
                if (currentState.showStopDialog) {
                    val phase = currentState.stopDialogPhase
                    dismissStopDialog(resume = false)
                    if (phase == JunkCleanPhase.Cleaning) stopCleaning.set(true) else requestExit()
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
                val files = currentState.session.pendingCleanFiles
                update(JunkCleanFilesSelected(emptyList()))
                if (files.isNotEmpty()) runCleaning(files)
            }
        }
    }

    private fun handleBack() {
        if (currentState.showStopDialog) return
        when (currentState.phase) {
            JunkCleanPhase.Scanning, JunkCleanPhase.Cleaning -> requestStopDialog()
            else -> requestExit()
        }
    }

    private fun requestExit() {
        if (exitEffectSent) return
        exitEffectSent = true
        scanJob?.cancel()
        cleaningJob?.cancel()
        sendEvent(JunkCleanEvent.Exit)
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
        update(JunkCleanFilesSelected(selectedFiles))
        permissionPrevious = currentState
        update(JunkPermissionCheckStarted(JunkOperation.Clean))
    }

    private fun runCleaning(files: List<JunkFileModel>) {
        stopCleaning.set(false)
        cleaningJob =
            viewModelScope.launch {
                try {
                    update(JunkCleaningRequested)
                    if (currentState.phase != JunkCleanPhase.Cleaning) return@launch

                    val cleaningStartedAt = System.currentTimeMillis()
                    val execution = executeCleaning(files) ?: return@launch
                    update(JunkExecutionUpdated(execution))
                    if (stopCleaning.get() || execution.directFailedFiles.isNotEmpty()) {
                        publishStoppedPreview(execution)
                        return@launch
                    }
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    finishCleaning(execution)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    update(JunkFailed(JunkError.CleanFailed))
                }
            }
    }

    private suspend fun executeCleaning(files: List<JunkFileModel>): JunkCleanExecution? {
        val outcomes =
            withContext(ioDispatcher) { cleanRepository.deleteFiles(files) { stopCleaning.get() } }.getOrElse {
                update(JunkFailed(JunkError.CleanFailed))
                return null
            }
        currentCoroutineContext().ensureActive()
        val (processed, failed) = outcomes.partition { it.processed }
        if (failed.any { it.status == DeleteStatus.AuthorizationCancelled }) stopCleaning.set(true)
        val memoryResult =
            if (stopCleaning.get() || failed.isNotEmpty()) {
                null
            } else {
                withContext(ioDispatcher) { cleanRepository.cleanMemory() }.getOrNull()
            }
        currentCoroutineContext().ensureActive()
        return JunkCleanExecution(
            directCleanedFiles = processed.map { it.file },
            directFailedFiles = failed.filter { it.status != DeleteStatus.AuthorizationCancelled }.map { it.file },
            directFreedSpace = processed.sumOf { it.freedBytes },
            memoryResult = memoryResult,
        )
    }

    private fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        cleaningJob?.cancel()
        cleaningJob = null
        update(JunkActiveOperationCancelled)
    }

    private suspend fun publishStoppedPreview(execution: JunkCleanExecution) {
        val previous = currentState.session.lastScan ?: return
        val processed = execution.directCleanedFiles.toSet()
        val remaining = previous.files.filterNot { it in processed }
        val remainingPaths = remaining.map { it.path }.toSet()
        val refreshed =
            if (execution.directFailedFiles.isEmpty()) {
                null
            } else {
                withContext(ioDispatcher) { cleanRepository.scan { }.getOrNull() }
            }
        val files = previous.copy(files = refreshed?.files?.filter { it.path in remainingPaths } ?: remaining)
        val selected =
            currentState.groups
                .flatMap { it.items }
                .filter { it.isChecked }
                .map { it.file }
                .toSet()
        val summary =
            CleanupSummaryModel(
                freedSpace = execution.directFreedSpace,
                cleanedCount = processed.size,
                failedCount = execution.directFailedFiles.size,
            )
        update(JunkCleaningStopped(files, selected, summary))
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

    private suspend fun finishCleaning(execution: JunkCleanExecution) {
        currentCoroutineContext().ensureActive()
        val cleanedFiles = execution.directCleanedFiles.toSet()
        if (stopCleaning.get()) {
            publishStoppedPreview(execution)
            return
        }
        val memoryResult = execution.memoryResult
        val summary =
            CleanupSummaryModel(
                freedSpace = execution.directFreedSpace,
                cleanedCount = cleanedFiles.size,
                memoryFreedBytes = memoryResult?.freedBytes ?: 0L,
            )

        val remaining =
            currentState.session.lastScan?.let { result ->
                result.copy(files = result.files.filterNot { it in cleanedFiles })
            }

        update(JunkCleaningCompleted(summary, remaining))
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
