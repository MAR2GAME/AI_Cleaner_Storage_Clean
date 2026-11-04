package com.aicleanerstorage.spaceclean.feature.junkclean

import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.junkclean.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.model.MemoryCleanResult
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanSessionStore
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkAuthorizedDeleteResult
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkDeleteOutcome
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val createDeleteRequest: (Context, List<String>) -> PendingIntent? = { context, uris ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.createDeleteRequest(context.contentResolver, uris.map(Uri::parse))
        } else {
            null
        }
    },
) : ViewModel() {
    private data class CleaningExecutionState(
        val pendingAuthorizationOutcomes: List<JunkDeleteOutcome> = emptyList(),
        val directCleanedFiles: List<JunkFile> = emptyList(),
        val directFailedFiles: List<JunkFile> = emptyList(),
        val directFreedSpace: Long = 0L,
        val memoryResult: MemoryCleanResult? = null,
    )

    private val FINISHING_ANIMATION_MILLIS = 800L

    private val _uiState = MutableStateFlow(JunkCleanUiState())
    val uiState: StateFlow<JunkCleanUiState> = _uiState.asStateFlow()

    private val effectsChannel = Channel<JunkCleanEffect>(Channel.BUFFERED)
    val effects = effectsChannel.receiveAsFlow()

    private val operationEventsChannel = Channel<FeatureOperationEvent>(Channel.BUFFERED)
    val operationEvents = operationEventsChannel.receiveAsFlow()

    private fun trackOperationEvent(event: FeatureOperationEvent) {
        operationEventsChannel.trySend(event)
    }

    private var progressJob: Job? = null
    private var scanJob: Job? = null
    private var cleaningJob: Job? = null
    private var exitEffectSent = false
    private var cleaningExecutionState = CleaningExecutionState()

    private fun dispatch(action: JunkCleanAction) {
        if (exitEffectSent) return
        _uiState.value = JunkCleanReducer.reduce(_uiState.value, action)
        if (action is JunkCleanAction.PermissionDenied) {
            exitEffectSent = true
            effectsChannel.trySend(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected))
        }
    }

    fun startScanIfNeeded() {
        if (_uiState.value.phase != JunkCleanPhase.Idle) return
        startScanInternal(resetSession = true)
    }

    fun retryScan() {
        startScanInternal(resetSession = true)
    }

    fun toggleItemSelection(itemId: String) {
        if (exitEffectSent) return
        val previous = _uiState.value
        dispatch(JunkCleanAction.ToggleItem(itemId))
        if (_uiState.value !== previous) trackPreviewPublished()
    }

    fun toggleCategorySelection(categories: List<JunkCategory>) {
        if (exitEffectSent) return
        val previous = _uiState.value
        dispatch(JunkCleanAction.ToggleCategories(categories))
        if (_uiState.value !== previous) trackPreviewPublished()
    }

    fun startCleaning(context: Context) {
        if (exitEffectSent) return
        val appContext = context.applicationContext ?: context
        val requestContext = context
        val state = _uiState.value.takeIf { it.phase == JunkCleanPhase.Preview } ?: return
        val selectedItems = state.groups.flatMap { it.items }.filter { it.isChecked }
        if (selectedItems.isEmpty()) {
            dispatch(JunkCleanAction.CleaningRequested)
            return
        }
        if (cleaningJob?.isActive == true) return
        val previous = state
        dispatch(JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Clean))

        cleaningJob =
            viewModelScope.launch {
                try {
                    when (ensureFilePermission()) {
                        PermissionResult.Granted -> Unit
                        PermissionResult.Busy -> {
                            _uiState.value = previous
                            dispatch(JunkCleanAction.PermissionBusy(JunkPermissionRequest.Clean))
                            return@launch
                        }
                        PermissionResult.Denied -> {
                            _uiState.value = previous
                            dispatch(JunkCleanAction.PermissionDenied(JunkPermissionRequest.Clean))
                            return@launch
                        }
                    }

                    _uiState.value = previous
                    dispatch(JunkCleanAction.CleaningRequested)
                    if (_uiState.value.phase != JunkCleanPhase.Cleaning) return@launch

                    trackOperationEvent(FeatureOperationEvent.OperationStarted(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN))
                    val cleaningStartedAt = System.currentTimeMillis()
                    val outcomes =
                        withContext(ioDispatcher) {
                            cleanRepository.deleteFileItems(selectedItems)
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
                    val request = if (uris.isNotEmpty()) createDeleteRequest(requestContext, uris) else null
                    if (request != null) {
                        val message = appContext.resources.getString(R.string.result_confirm_system_deletion, uris.size)
                        dispatch(JunkCleanAction.DeleteAuthorizationRequested(message))
                        effectsChannel.send(JunkCleanEffect.RequestDeleteAuthorization(request))
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
                    dispatch(JunkCleanAction.Failed(R.string.result_clean_error, error.message))
                }
            }
    }

    fun handleAuthorizationResult(approved: Boolean) {
        if (_uiState.value.phase != JunkCleanPhase.AwaitingAuthorization) return
        val pending = cleaningExecutionState.pendingAuthorizationOutcomes
        if (pending.isEmpty()) return

        dispatch(JunkCleanAction.DeleteAuthorizationHandled)
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
                    dispatch(JunkCleanAction.Failed(R.string.result_clean_error_after_authorization, error.message))
                }
            }
    }

    fun clearResult() {
        sharedState.clearCleanResults()
        dispatch(JunkCleanAction.ResultCleared)
    }

    fun clearSharedResults() {
        sharedState.clearCleanResults()
    }

    fun showResult() {
        if (!viewModelScope.isActive) return
        dispatch(JunkCleanAction.ResultAnimationCompleted)
    }

    private fun startScanInternal(resetSession: Boolean) {
        if (exitEffectSent || cleaningJob?.isActive == true) return
        when (val phase = _uiState.value.phase) {
            JunkCleanPhase.Idle, JunkCleanPhase.Preview -> Unit
            is JunkCleanPhase.Failed -> if (phase.operation != JunkOperation.Scan) return
            else -> return
        }
        if (scanJob?.isActive == true) return
        val previous = _uiState.value
        dispatch(JunkCleanAction.PermissionCheckStarted(JunkPermissionRequest.Scan))
        scanJob =
            viewModelScope.launch {
                try {
                    when (ensureFilePermission()) {
                        PermissionResult.Granted -> Unit
                        PermissionResult.Busy -> {
                            _uiState.value = previous
                            dispatch(JunkCleanAction.PermissionBusy(JunkPermissionRequest.Scan))
                            return@launch
                        }
                        PermissionResult.Denied -> {
                            dispatch(JunkCleanAction.PermissionDenied(JunkPermissionRequest.Scan))
                            return@launch
                        }
                    }

                    if (resetSession) {
                        sharedState.clear()
                    }
                    val scanStartedAt = System.currentTimeMillis()
                    observeProgress()
                    trackOperationEvent(FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN))
                    dispatch(JunkCleanAction.ScanStarted)
                    val result = withContext(ioDispatcher) {
                        cleanRepository.performFullScan()
                    }
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    dispatch(JunkCleanAction.ScanCompleted(result))
                    delayRemainingScanAnimation(scanStartedAt)
                    loadPreview(result)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    progressJob?.cancel()
                    dispatch(JunkCleanAction.Failed(R.string.scan_failed, error.message, duringScan = true))
                }
            }
    }

    fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        progressJob?.cancel()
        progressJob = null
        cleaningJob?.cancel()
        cleaningJob = null
        dispatch(JunkCleanAction.ActiveOperationCancelled)
        cleaningExecutionState = CleaningExecutionState()
    }

    private suspend fun ensureFilePermission(): PermissionResult {
        return permissionGateway.ensure(PermissionNeed.StorageFiles).also {
            currentCoroutineContext().ensureActive()
        }
    }

    fun cancelCleaningAndReturnToPreview() {
        cleaningJob?.cancel()
        cleaningJob = null
        cleaningExecutionState = CleaningExecutionState()
        dispatch(JunkCleanAction.CleaningCancelled)
    }

    fun requestStopDialog() {
        val phase = _uiState.value.phase
        when (phase) {
            JunkCleanPhase.Scanning -> cancelActiveOperation()
            JunkCleanPhase.Cleaning,
            JunkCleanPhase.AwaitingAuthorization,
            -> cancelCleaningAndReturnToPreview()
            else -> return
        }
        dispatch(JunkCleanAction.StopDialogShown(phase))
    }

    fun dismissStopDialog(resume: Boolean) {
        val stoppedPhase = _uiState.value.stopDialogPhase
        dispatch(JunkCleanAction.StopDialogDismissed)
        if (resume && stoppedPhase == JunkCleanPhase.Scanning) {
            startScanIfNeeded()
        }
    }

    private fun observeProgress() {
        progressJob?.cancel()
        progressJob =
            viewModelScope.launch {
                sharedState.scanProgress.collect { progress ->
                    if (_uiState.value.phase != JunkCleanPhase.Scanning) return@collect
                    val currentResult = sharedState.scanResult.value
                    val foundSize = currentResult?.totalSize ?: progress.foundSize
                    dispatch(
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
            dispatch(JunkCleanAction.PreviewUnavailable)
            return
        }

        dispatch(JunkCleanAction.PreviewLoaded(scanResult))
        trackPreviewPublished()
    }

    private fun trackPreviewPublished() {
        val hasGroups = _uiState.value.groups.any { it.items.isNotEmpty() }
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
        dispatch(JunkCleanAction.CleaningCompleted(summary))
        delay(FINISHING_ANIMATION_MILLIS)
        if (_uiState.value.phase == JunkCleanPhase.CompleteAnimation) {
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
