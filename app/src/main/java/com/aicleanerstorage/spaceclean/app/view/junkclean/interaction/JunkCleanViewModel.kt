package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionChecker

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.domain.model.CleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryCleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.CleanupSummaryModel
import com.aicleanerstorage.spaceclean.domain.port.CleanRepository
import com.aicleanerstorage.spaceclean.domain.model.JunkDeleteOutcomeModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SCAN_PREVIEW_MIN_MILLIS = 1_000L
private const val CLEANING_ANIMATION_MIN_MILLIS = 1_600L

class JunkCleanViewModel(
    private val cleanRepository: CleanRepository,
    private val permissionChecker: PermissionChecker? = null,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanAction, JunkCleanEvent>(
    initialState = JunkCleanUiState(),
) {
    private data class CleaningExecutionState(
        val pendingAuthorizationOutcomes: List<JunkDeleteOutcomeModel> = emptyList(),
        val directCleanedFiles: List<JunkFileModel> = emptyList(),
        val directFailedFiles: List<JunkFileModel> = emptyList(),
        val directFreedSpace: Long = 0L,
        val memoryResult: MemoryCleanResultModel? = null,
    )

    private val FINISHING_ANIMATION_MILLIS = 800L

    private var progressJob: Job? = null
    private var scanJob: Job? = null
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var cleaningExecutionState = CleaningExecutionState()

    override fun onAction(intent: JunkCleanAction) {
        if (!viewModelScope.isActive) return
        if (exitEffectSent) return

        when (intent) {
            JunkCleanAction.Entered -> startScanIfNeeded()
            JunkCleanAction.ScanRequested -> startScanInternal(resetSession = true)
            JunkCleanAction.CleanClicked -> startCleaning()
            is JunkCleanAction.ItemToggled -> toggleItemSelection(intent.itemId)
            is JunkCleanAction.CategoriesToggled -> toggleCategorySelection(intent.categories)
            is JunkCleanAction.DeleteAuthorizationResultReceived -> handleAuthorizationResult(intent.approved)
            JunkCleanAction.CompletionAdFinished -> {
                if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
                    dispatch(JunkResultAnimationCompleted)
                }
            }
            JunkCleanAction.BackClicked -> handleBack()
            JunkCleanAction.ContinueClicked -> {
                if (currentState.phase == JunkCleanPhase.Complete) {
                    requestExit(JunkExitTarget.HomeAfterComplete)
                }
            }
            JunkCleanAction.StopConfirmed -> {
                if (currentState.showStopDialog) {
                    dismissStopDialog(resume = false)
                    requestExit()
                }
            }
            JunkCleanAction.StopDismissed -> {
                if (currentState.showStopDialog) dismissStopDialog(resume = true)
            }
            else -> reduce(intent)
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

    private fun requestExit(
        target: JunkExitTarget = JunkExitTarget.Back,
    ) {
        if (exitEffectSent) return
        exitEffectSent = true
        scanJob?.cancel()
        cleaningJob?.cancel()
        progressJob?.cancel()
        sendEvent(JunkCleanEvent.Exit(target))
    }

    private fun reduce(action: JunkCleanAction) {
        setState { JunkCleanReducer.reduce(this, action) }
    }

    private fun startScanIfNeeded() {
        if (currentState.phase != JunkCleanPhase.Idle) return
        startScanInternal(resetSession = true)
    }

    private fun toggleItemSelection(itemId: String) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanAction.ItemToggled(itemId))
        if (currentState !== previous) trackPreviewPublished()
    }

    private fun toggleCategorySelection(categories: List<JunkCategory>) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanAction.CategoriesToggled(categories))
        if (currentState !== previous) trackPreviewPublished()
    }

    private fun startCleaning() {
        if (exitEffectSent) return
        val state = currentState.takeIf { it.phase == JunkCleanPhase.Preview } ?: return
        val selectedFiles = state.groups.flatMap { it.items }.filter { it.isChecked }.map { it.junkFile }
        if (selectedFiles.isEmpty()) {
            dispatch(JunkCleaningRequested)
            return
        }
        if (cleaningJob?.isActive == true) return
        val previous = state
        dispatch(JunkPermissionCheckStarted(JunkPermissionRequest.Clean))

        cleaningJob =
            viewModelScope.launch {
                try {
                    if (!checkPermission(JunkPermissionRequest.Clean, previous)) return@launch
                    dispatch(JunkCleaningRequested)
                    if (currentState.phase != JunkCleanPhase.Cleaning) return@launch

                    val cleaningStartedAt = System.currentTimeMillis()
                    val outcomes = withContext(ioDispatcher) { cleanRepository.deleteFileItems(selectedFiles) }
                    currentCoroutineContext().ensureActive()
                    val deleted = outcomes.filter { it.deleted }
                    val pending = outcomes.filter { !it.deleted && it.authorizationUri != null }
                    val failed = outcomes.filter { !it.deleted && it.authorizationUri == null }
                    val memoryResult = withContext(ioDispatcher) { cleanRepository.cleanMemory() }
                    currentCoroutineContext().ensureActive()

                    cleaningExecutionState =
                        CleaningExecutionState(
                            pendingAuthorizationOutcomes = pending,
                            directCleanedFiles = deleted.map { it.junkFile },
                            directFailedFiles = failed.map { it.junkFile },
                            directFreedSpace = deleted.sumOf { it.freedBytes },
                            memoryResult = memoryResult,
                        )

                    val uris = pending.mapNotNull { it.authorizationUri }.distinct()
                    if (uris.isNotEmpty()) {
                        dispatch(JunkDeleteAuthorizationRequested(uris.size))
                        sendEvent(JunkCleanEvent.RequestDeleteAuthorization(uris.toList()))
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
                    dispatch(JunkFailed(R.string.result_clean_error, error.message))
                }
            }
    }

    private fun handleAuthorizationResult(approved: Boolean) {
        if (currentState.phase != JunkCleanPhase.AwaitingAuthorization) return
        val pending = cleaningExecutionState.pendingAuthorizationOutcomes
        if (pending.isEmpty()) return

        dispatch(JunkDeleteAuthorizationHandled)
        val cleaningStartedAt = System.currentTimeMillis()
        cleaningJob?.cancel()
        cleaningJob =
            viewModelScope.launch {
                try {
                    val authorizedResult =
                        withContext(ioDispatcher) {
                            if (approved) {
                                cleanRepository.finalizeAuthorizedDeletes(pending)
                            } else {
                                CleanResultModel(
                                    cleanedFiles = emptyList(),
                                    failedFiles = pending.map { it.junkFile },
                                    freedSpace = 0L,
                                )
                            }
                        }
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    finishCleaning(
                        extraCleanedFiles = authorizedResult.cleanedFiles,
                        extraFailedFiles = authorizedResult.failedFiles,
                        extraFreedSpace = authorizedResult.freedSpace,
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    dispatch(JunkFailed(R.string.result_clean_error_after_authorization, error.message))
                }
            }
    }

    private fun startScanInternal(resetSession: Boolean) {
        if (exitEffectSent || cleaningJob?.isActive == true) return
        when (val phase = currentState.phase) {
            JunkCleanPhase.Idle, JunkCleanPhase.Preview -> Unit
            is JunkCleanPhase.Failed -> if (phase.operation != JunkOperation.Scan) return
            else -> return
        }
        if (scanJob?.isActive == true) return
        val previous = currentState
        dispatch(JunkPermissionCheckStarted(JunkPermissionRequest.Scan))
        scanJob =
            viewModelScope.launch {
                try {
                    if (!checkPermission(JunkPermissionRequest.Scan, previous)) return@launch

                    if (resetSession) {
                        cleanRepository.clearSession()
                    }
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
                    dispatch(JunkFailed(R.string.scan_failed, error.message, duringScan = true))
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

    private suspend fun checkPermission(
        request: JunkPermissionRequest,
        previous: JunkCleanUiState,
    ): Boolean {
        currentCoroutineContext().ensureActive()
        return if (permissionChecker?.isGranted(AppPermission.StorageFiles) != false) {
            dispatch(JunkPermissionCheckRestored(request, previous))
            true
        } else {
            dispatch(JunkPermissionDenied(request))
            requestExit()
            false
        }
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
            JunkCleanPhase.Cleaning,
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

    private fun loadPreview(scanResult: ScanResultModel?) {
        if (scanResult == null) {
            dispatch(JunkPreviewLoadFailed)
            return
        }

        dispatch(JunkPreviewLoaded(scanResult))
        trackPreviewPublished()
    }

    private fun trackPreviewPublished() {
        val hasGroups = currentState.groups.any { it.items.isNotEmpty() }
    }

    private suspend fun finishCleaning(
        extraCleanedFiles: List<JunkFileModel>,
        extraFailedFiles: List<JunkFileModel>,
        extraFreedSpace: Long,
    ) {
        currentCoroutineContext().ensureActive()
        val cleanedFiles = cleaningExecutionState.directCleanedFiles + extraCleanedFiles
        val failedFiles = cleaningExecutionState.directFailedFiles + extraFailedFiles
        val freedSpace = cleaningExecutionState.directFreedSpace + extraFreedSpace
        val result =
            CleanResultModel(
                cleanedFiles = cleanedFiles,
                freedSpace = freedSpace,
                failedFiles = failedFiles,
            )
        val memoryResult = cleaningExecutionState.memoryResult
        val summary =
            CleanupSummaryModel(
                freedSpace = result.freedSpace,
                cleanedCount = result.successCount,
                failedCount = result.failedCount,
                memoryFreedBytes = memoryResult?.freedBytes ?: 0L,
                memoryProcessesKilled = memoryResult?.killedCount ?: 0,
            )

        cleanRepository.removeCleanedFiles(result.cleanedFiles)

        cleaningExecutionState = CleaningExecutionState()
        dispatch(JunkCleaningCompleted(summary))
        delay(FINISHING_ANIMATION_MILLIS)
        if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
            dispatch(JunkCleanAction.CompletionAdFinished)
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
