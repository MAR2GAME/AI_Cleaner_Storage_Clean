package com.aicleanerstorage.spaceclean.feature.junkclean.presentation

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanProgress

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.MemoryCleanResult
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanSessionStore
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkAuthorizedDeleteResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkDeleteOutcome
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
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
    interceptors: List<IntentInterceptor>,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanIntent, JunkCleanEffect>(
    initialState = JunkCleanUiState(),
    interceptors = interceptors,
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
                    reduce(JunkCleanAction.ResultAnimationCompleted)
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

    private fun reduce(action: JunkCleanAction) {
        updateState { JunkCleanReducer.reduce(this, action) }
    }

    private fun startScanIfNeeded() {
        if (currentState.phase != JunkCleanPhase.Idle) return
        startScanInternal(resetSession = true)
    }

    private fun toggleItemSelection(itemId: String) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanAction.ToggleItem(itemId))
        if (currentState !== previous) trackPreviewPublished()
    }

    private fun toggleCategorySelection(categories: List<JunkCategory>) {
        if (exitEffectSent) return
        val previous = currentState
        reduce(JunkCleanAction.ToggleCategories(categories))
        if (currentState !== previous) trackPreviewPublished()
    }

    private fun startCleaning() {
        if (exitEffectSent) return
        val state = currentState.takeIf { it.phase == JunkCleanPhase.Preview } ?: return
        val selectedFiles = state.groups.flatMap { it.items }.filter { it.isChecked }.map { it.junkFile }
        if (selectedFiles.isEmpty()) {
            reduce(JunkCleanAction.CleaningRequested)
            return
        }
        if (cleaningJob?.isActive == true) return
        val previous = state
        reduce(JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Clean))

        cleaningJob =
            viewModelScope.launch {
                try {
                    if (!checkPermission(JunkPermissionRequest.Clean, previous)) return@launch
                    reduce(JunkCleanAction.CleaningRequested)
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
                        reduce(JunkCleanAction.DeleteAuthorizationRequested(uris.size))
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
                    reduce(JunkCleanAction.Failed(R.string.result_clean_error, error.message))
                }
            }
    }

    private fun handleAuthorizationResult(approved: Boolean) {
        if (currentState.phase != JunkCleanPhase.AwaitingAuthorization) return
        val pending = cleaningExecutionState.pendingAuthorizationOutcomes
        if (pending.isEmpty()) return

        reduce(JunkCleanAction.DeleteAuthorizationHandled)
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
                    reduce(JunkCleanAction.Failed(R.string.result_clean_error_after_authorization, error.message))
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
        reduce(JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Scan))
        scanJob =
            viewModelScope.launch {
                try {
                    if (!checkPermission(JunkPermissionRequest.Scan, previous)) return@launch

                    if (resetSession) {
                        sharedState.clear()
                    }
                    val scanStartedAt = System.currentTimeMillis()
                    reduce(JunkCleanAction.ScanStarted)
                    observeProgress()
                    trackOperationEvent(FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN))
                    val result = withContext(ioDispatcher) {
                        cleanRepository.performFullScan()
                    }
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    reduce(JunkCleanAction.ScanCompleted(result))
                    delayRemainingScanAnimation(scanStartedAt)
                    loadPreview(result)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    reduce(JunkCleanAction.Failed(R.string.scan_failed, error.message, duringScan = true))
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
        reduce(JunkCleanAction.ActiveOperationCancelled)
        cleaningExecutionState = CleaningExecutionState()
    }

    private suspend fun checkPermission(
        request: JunkPermissionRequest,
        previous: JunkCleanUiState,
    ): Boolean {
        val result = resolvePolicies(PermissionNeed.StorageFiles)
        currentCoroutineContext().ensureActive()
        return when (result) {
            InterceptResult.Continue -> {
                reduce(JunkCleanAction.PermissionCheckRestored(request, previous))
                true
            }
            InterceptResult.Busy -> {
                reduce(JunkCleanAction.PermissionCheckRestored(request, previous))
                false
            }
            InterceptResult.Block -> {
                reduce(JunkCleanAction.PermissionDenied(request))
                requestExit(FeatureExitReason.PermissionRejected)
                false
            }
            InterceptResult.NotHandled ->
                error("resolvePolicies must resolve unhandled policies to Block")
        }
    }

    private fun cancelCleaningAndReturnToPreview() {
        cleaningJob?.cancel()
        cleaningJob = null
        cleaningExecutionState = CleaningExecutionState()
        reduce(JunkCleanAction.CleaningCancelled)
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
        reduce(JunkCleanAction.StopDialogShown(phase))
    }

    private fun dismissStopDialog(resume: Boolean) {
        val stoppedPhase = currentState.stopDialogPhase
        reduce(JunkCleanAction.StopDialogDismissed)
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
                        JunkCleanAction.ScanProgressUpdated(
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
            reduce(JunkCleanAction.PreviewUnavailable)
            return
        }

        reduce(JunkCleanAction.PreviewLoaded(scanResult))
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
        reduce(JunkCleanAction.CleaningCompleted(summary))
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
