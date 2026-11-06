package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.MemoryCleanResult
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanSessionStore
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkAuthorizedDeleteResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkDeleteOutcome
import com.aicleanerstorage.spaceclean.feature.junkclean.intent.JunkCleanOperation
import com.aicleanerstorage.spaceclean.feature.junkclean.intent.JunkCleanPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
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
    private val sharedState: CleanSessionStore,
    private val permissionGateway: PermissionGateway,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanIntent, JunkCleanEffect>(
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

    private fun trackOperationEvent(event: FeatureOperationEvent) {
        emitEffect(JunkCleanEffect.Operation(event))
    }

    private var progressJob: Job? = null
    private var scanJob: Job? = null
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var cleaningExecutionState = CleaningExecutionState()
    private val permissionGate = JunkCleanPermissionGate(permissionGateway)

    override fun onIntent(intent: JunkCleanIntent) {
        if (!viewModelScope.isActive) return
        if (intent == JunkCleanIntent.ExitFinished) {
            sharedState.clearCleanResults()
            return
        }
        if (exitEffectSent) return

        when (intent) {
            JunkCleanIntent.Entered -> startScanIfNeeded()
            JunkCleanIntent.ScanRequested -> startScanInternal(resetSession = true)
            JunkCleanIntent.CleanSelected -> startCleaning()
            is JunkCleanIntent.ToggleItem -> toggleItemSelection(intent.itemId)
            is JunkCleanIntent.ToggleCategories -> toggleCategorySelection(intent.categories)
            is JunkCleanIntent.DeleteAuthorizationResult -> handleAuthorizationResult(intent.approved)
            JunkCleanIntent.CompletionAdFinished -> {
                if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
                    dispatch(JunkResultAnimationCompleted)
                }
            }
            JunkCleanIntent.Back -> handleBack()
            JunkCleanIntent.ContinueFromResult -> {
                if (currentState.phase == JunkCleanPhase.Complete) {
                    requestExit(FeatureExitReason.Return, JunkExitTarget.HomeAfterComplete)
                }
            }
            JunkCleanIntent.QuitStoppedOperation -> {
                if (currentState.showStopDialog) {
                    dismissStopDialog(resume = false)
                    requestExit(FeatureExitReason.Return)
                }
            }
            JunkCleanIntent.ResumeStoppedOperation -> {
                if (currentState.showStopDialog) dismissStopDialog(resume = true)
            }
            JunkCleanIntent.ExitFinished -> Unit
            else -> reduce(intent)
        }
    }

    private fun handleBack() {
        if (currentState.showStopDialog) return
        when (currentState.phase) {
            JunkCleanPhase.Scanning, JunkCleanPhase.Cleaning -> requestStopDialog()
            JunkCleanPhase.CompleteAnimation, JunkCleanPhase.Complete ->
                requestExit(FeatureExitReason.Return, JunkExitTarget.HomeAfterComplete)
            else -> requestExit(FeatureExitReason.Return)
        }
    }

    private fun requestExit(
        reason: FeatureExitReason,
        target: JunkExitTarget = JunkExitTarget.Back,
    ) {
        if (exitEffectSent) return
        exitEffectSent = true
        scanJob?.cancel()
        cleaningJob?.cancel()
        progressJob?.cancel()
        emitEffect(JunkCleanEffect.Exit(reason, target))
    }

    private fun reduce(action: JunkCleanIntent) {
        updateState { JunkCleanReducer.reduce(this, action) }
    }

    private fun startScanIfNeeded() {
        if (currentState.phase != JunkCleanPhase.Idle) return
        startScanInternal(resetSession = true)
    }

    private fun toggleItemSelection(itemId: String) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanIntent.ToggleItem(itemId))
        if (currentState !== previous) trackPreviewPublished()
    }

    private fun toggleCategorySelection(categories: List<JunkCategory>) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanIntent.ToggleCategories(categories))
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

                    trackOperationEvent(FeatureOperationEvent.OperationStarted(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN))
                    val cleaningStartedAt = System.currentTimeMillis()
                    val outcomes =
                        withContext(ioDispatcher) {
                            cleanRepository.deleteFileItems(selectedFiles)
                        }
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
                        emitEffect(JunkCleanEffect.RequestDeleteAuthorization(uris.toList()))
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
                    trackOperationEvent(FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, success = false))
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
                                JunkAuthorizedDeleteResult(
                                    cleanedFiles = emptyList(),
                                    failedFiles = pending.map { it.junkFile },
                                    freedBytes = 0L,
                                )
                            }
                        }
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    finishCleaning(
                        extraCleanedFiles = authorizedResult.cleanedFiles,
                        extraFailedFiles = authorizedResult.failedFiles,
                        extraFreedSpace = authorizedResult.freedBytes,
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    trackOperationEvent(FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, success = false))
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
                        sharedState.clear()
                    }
                    val scanStartedAt = System.currentTimeMillis()
                    dispatch(JunkScanStarted)
                    observeProgress()
                    trackOperationEvent(FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN))
                    val result = withContext(ioDispatcher) {
                        cleanRepository.performFullScan()
                    }
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
        val result = permissionGate.checkOperation(
            when (request) {
                JunkPermissionRequest.Scan -> JunkCleanOperation.Scan
                JunkPermissionRequest.Clean -> JunkCleanOperation.Clean(emptyList())
            },
        )
        currentCoroutineContext().ensureActive()
        return when (result) {
            GateResult.Allow -> {
                dispatch(JunkPermissionCheckRestored(request, previous))
                true
            }
            GateResult.Busy -> {
                dispatch(JunkPermissionCheckRestored(request, previous))
                false
            }
            is GateResult.Deny -> {
                dispatch(JunkPermissionDenied(request))
                requestExit(FeatureExitReason.PermissionRejected)
                false
            }
            GateResult.Cancelled -> false
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
                sharedState.scanProgress.collect { progress ->
                    if (currentState.phase != JunkCleanPhase.Scanning) return@collect
                    val currentResult = sharedState.scanResult.value
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
            dispatch(JunkPreviewUnavailable)
            return
        }

        dispatch(JunkPreviewLoaded(scanResult))
        trackPreviewPublished()
    }

    private fun trackPreviewPublished() {
        val hasGroups = currentState.groups.any { it.items.isNotEmpty() }
        trackOperationEvent(FeatureOperationEvent.ScanFinished(FeatureKey.JUNK_CLEAN, hasGroups))
    }

    private suspend fun finishCleaning(
        extraCleanedFiles: List<JunkFile>,
        extraFailedFiles: List<JunkFile>,
        extraFreedSpace: Long,
    ) {
        currentCoroutineContext().ensureActive()
        val cleanedFiles = cleaningExecutionState.directCleanedFiles + extraCleanedFiles
        val failedFiles = cleaningExecutionState.directFailedFiles + extraFailedFiles
        val freedSpace = cleaningExecutionState.directFreedSpace + extraFreedSpace
        val result =
            CleanResult(
                cleanedFiles = cleanedFiles,
                freedSpace = freedSpace,
                failedFiles = failedFiles,
            )
        val memoryResult = cleaningExecutionState.memoryResult
        val summary =
            CleanupSummary(
                freedSpace = result.freedSpace,
                cleanedCount = result.successCount,
                failedCount = result.failedCount,
                memoryFreedBytes = memoryResult?.freedBytes ?: 0L,
                memoryProcessesKilled = memoryResult?.killedCount ?: 0,
            )

        sharedState.removeCleanedFiles(cleanedFiles)
        sharedState.setCleanResult(result)
        if (memoryResult != null) {
            sharedState.setMemoryResult(memoryResult)
        }
        sharedState.setCleanupSummary(summary)

        cleaningExecutionState = CleaningExecutionState()
        dispatch(JunkCleaningCompleted(summary))
        delay(FINISHING_ANIMATION_MILLIS)
        if (currentState.phase == JunkCleanPhase.CompleteAnimation) {
            trackOperationEvent(FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, success = true))
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
