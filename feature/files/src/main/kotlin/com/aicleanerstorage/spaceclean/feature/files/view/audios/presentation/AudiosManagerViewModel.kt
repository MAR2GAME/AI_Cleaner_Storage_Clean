package com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.appString
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.fileScanFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.deletionFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleId
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleIds
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.openDetailIndex
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerExecutorFactory
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerMediaType
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AudiosManagerViewModel(
    private val repository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val testLoader: (((suspend () -> Unit)) -> Unit)? = null,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
    private val executorFactory: FileManagerExecutorFactory? = null
) : MviViewModel<AudiosManagerUiState, FileManagerIntent, FileManagerEffect>(AudiosManagerUiState()) {
    private val featureKey = FeatureKey.AUDIOS
    private var activeJob: Job? = null
    private val filePipeline by lazy { executorFactory?.pipeline(viewModelScope) }
    private var pendingResult: (() -> Unit)? = null
    private val stateUpdateLock = Any()

    private var exitRequested = false
    private fun trackOperation(event: FeatureOperationEvent) = emitEffect(FileManagerEffect.Operation(event))
    private fun exit(reason: FeatureExitReason, home: Boolean) {
        if (exitRequested) return
        exitRequested = true
        cancelActiveOperation()
        emitEffect(FileManagerEffect.Exit(reason, home))
    }

    override fun onIntent(intent: FileManagerIntent) {
        if (exitRequested) return
        when (intent) {
            is FileManagerIntent.Navigate -> emitEffect(FileManagerEffect.Navigate(intent.destination, intent.reset))
            FileManagerIntent.PermissionRejected -> exit(FeatureExitReason.PermissionRejected, home = false)
            FileManagerIntent.Back -> handleBack()
            FileManagerIntent.CloseDetailAndBack -> { closeDetail(); emitEffect(FileManagerEffect.Back) }

            is AudiosManagerStateChanged -> updateState { AudiosManagerReducer.reduce(this, intent) }
            is FileManagerIntent.ShowStopDialog -> showStopDialog(intent.blockedPhase)
            FileManagerIntent.DismissStopDialog -> dismissStopDialog()
            FileManagerIntent.CancelActiveOperation -> cancelActiveOperation()
            FileManagerIntent.CancelDeletingAndReturnToBrowsing -> cancelDeletingAndReturnToBrowsing()
            FileManagerIntent.CompletionAdFinished -> showPendingResult()
            FileManagerIntent.StartIfNeeded -> startIfNeeded()
            FileManagerIntent.Refresh -> refresh()
            is FileManagerIntent.SelectTab -> selectTab(intent.index)
            is FileManagerIntent.ToggleSelection -> toggleSelection(intent.id)
            is FileManagerIntent.ToggleVisibleIds -> toggleVisibleItems(intent.ids)
            is FileManagerIntent.OpenDetail -> {
                openDetail(intent.index)
                emitEffect(FileManagerEffect.Navigate(AppDestination.AudiosDetail(intent.index), reset = false))
            }
            FileManagerIntent.CloseDetail -> closeDetail()
            FileManagerIntent.RequestDelete -> requestDelete()
            FileManagerIntent.CancelDelete -> cancelDelete()
            FileManagerIntent.RejectSystemDelete -> rejectSystemDelete()
            FileManagerIntent.ClearError -> clearError()
            FileManagerIntent.DeleteSelectedFiles -> deleteSelectedFiles()
            FileManagerIntent.ContinueManaging -> continueManaging()
            FileManagerIntent.DeleteReady -> deleteSelectedFiles()
            else -> Unit
        }
    }

    private fun handleBack() {
        when {
            currentState.phase == FileOperationPhase.Deleting -> showStopDialog()
            currentState.phase == FileOperationPhase.Scanning -> showStopDialog(currentState.phase)
            currentState.phase == FileOperationPhase.ConfirmDelete -> cancelDelete()
            else -> exit(FeatureExitReason.Return, home = true)
        }
    }
    private fun changeState(transform: (AudiosManagerUiState) -> AudiosManagerUiState) {
        synchronized(stateUpdateLock) { dispatch(AudiosManagerStateChanged(transform(currentState))) }
    }
    private fun publishState(value: AudiosManagerUiState) {
        synchronized(stateUpdateLock) { dispatch(AudiosManagerStateChanged(value)) }
    }
    private fun showStopDialog(blockedPhase: FileOperationPhase? = null) {
        changeState { it.copy(showStopDialog = true, blockedPhase = blockedPhase) }
    }
    private fun dismissStopDialog() { changeState { it.copy(showStopDialog = false, blockedPhase = null) } }
    private fun cancelActiveOperation() {
        pendingResult = null
        activeJob?.cancel()
        activeJob = null
        dismissStopDialog()
    }
    private fun cancelDeletingAndReturnToBrowsing() { cancelActiveOperation(); onCancelDeletingPhase() }
    private fun showPendingResult() { val action = pendingResult ?: return; pendingResult = null; action() }
    private fun trackScanStarted() = trackOperation(FeatureOperationEvent.ScanStarted(featureKey))
    private fun trackScanFinished(hasResult: Boolean) = trackOperation(FeatureOperationEvent.ScanFinished(featureKey, hasResult))
    private fun launchOperation(block: suspend () -> Unit) {
        if (activeJob?.isActive == true) return
        testLoader?.let { it(block); return }
        activeJob = viewModelScope.launch(ioDispatcher) {
            try { block() } finally { if (activeJob == coroutineContext[Job]) activeJob = null }
        }
    }
    private suspend fun delayIfNeeded(millis: Long) { if (millis > 0) delay(millis) }
    private data class FileOperationOutcome(val freedBytes: Long = 0L, val changedCount: Int = 0)
    private fun runFileOperation(
        selectedFiles: List<ManagedFileItem>, action: OperationAction,
        onEmptySelection: () -> Unit, onStart: () -> Unit,
        operationDelayMillis: Long, completeDelayMillis: Long,
        operation: suspend () -> FileOperationOutcome,
        isSuccessful: (FileOperationOutcome) -> Boolean = { true },
        onCompleteAnimation: suspend (FileOperationOutcome) -> Unit,
        onResult: () -> Unit, onFailure: (Throwable) -> Unit,
    ) {
        if (activeJob?.isActive == true) return
        if (selectedFiles.isEmpty()) { onEmptySelection(); return }
        pendingResult = null
        onStart()
        trackOperation(FeatureOperationEvent.OperationStarted(featureKey, action))
        launchOperation {
            try {
                val result = operation()
                kotlinx.coroutines.currentCoroutineContext().ensureActive()
                if (!isSuccessful(result)) error(deletionFailedMessage())
                delayIfNeeded(operationDelayMillis)
                onCompleteAnimation(result)
                delayIfNeeded(completeDelayMillis)
                pendingResult = onResult
                trackOperation(FeatureOperationEvent.OperationFinished(featureKey, action, true))
            } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
            catch (error: Exception) {
                pendingResult = null
                trackOperation(FeatureOperationEvent.OperationFinished(featureKey, action, false))
                onFailure(error)
            }
        }
    }


    private fun startIfNeeded() {
        if (currentState.phase != FileOperationPhase.Scanning || currentState.items.isNotEmpty()) return
        refresh()
    }

    private fun refresh() {
        if (activeJob?.isActive == true) return
        trackScanStarted()
        publishState(AudiosManagerUiState(phase = FileOperationPhase.Scanning))
        launchOperation {
            runCatching { mapAudios(filePipeline?.load(FileManagerMediaType.Audios)?.files ?: repository.loadAudios()) }
                .onSuccess { audios ->
                    delayIfNeeded(scanDelayMillis)
                    trackScanFinished(hasResult = audios.isNotEmpty())
                    publishState(AudiosManagerUiState(
                        phase = if (audios.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
                        items = audios,
                        tabs = buildAudioTabs(audios),
                    ))
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    trackScanFinished(hasResult = false)
                    changeState { it.copy(phase = FileOperationPhase.NoResults, errorMessage = error.message ?: fileScanFailedMessage()) }
                }
        }
    }

    private fun selectTab(index: Int) {
        changeState {
            it.copy(
                selectedTabIndex = index.coerceAtLeast(0),
                selectedIds = emptySet(),
                detailStartIndex = null,
            )
        }
    }

    private fun toggleSelection(id: Int) {
        changeState { it.copy(selectedIds = toggleId(it.selectedIds, id)) }
    }

    private fun toggleVisibleItems(ids: Set<Int>) {
        changeState { it.copy(selectedIds = toggleIds(it.selectedIds, ids)) }
    }

    private fun openDetail(index: Int?) {
        changeState { it.copy(detailStartIndex = openDetailIndex(index)) }
    }

    private fun closeDetail() {
        changeState { it.copy(detailStartIndex = null) }
    }

    private fun requestDelete() {
        changeState {
            if (it.selectedIds.isNotEmpty()) {
                it.copy(phase = FileOperationPhase.ConfirmDelete)
            } else {
                it
            }
        }
    }

    private fun cancelDelete() {
        changeState { it.copy(phase = FileOperationPhase.Browsing) }
    }

    private fun rejectSystemDelete() {
        cancelDelete()
    }

    private fun clearError() {
        changeState { it.copy(errorMessage = null) }
    }

    private fun onCancelDeletingPhase() {
        changeState { current ->
            if (current.phase == FileOperationPhase.Deleting) {
                current.copy(phase = FileOperationPhase.Browsing)
            } else {
                current
            }
        }
    }

    private fun deleteSelectedFiles() {
        val selectedFiles = currentState.selectedFiles
        runFileOperation(
            selectedFiles = selectedFiles,
            action = OperationAction.DELETE,
            onEmptySelection = { changeState { it.copy(phase = FileOperationPhase.Browsing) } },
            onStart = { changeState { it.copy(phase = FileOperationPhase.Deleting, detailStartIndex = null) } },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = {
                val result = filePipeline?.delete(selectedFiles) ?: repository.deleteFiles(selectedFiles)
                FileOperationOutcome(freedBytes = result.freedBytes, changedCount = result.deletedCount)
            },
            isSuccessful = { it.changedCount > 0 },
            onCompleteAnimation = { outcome ->
                val rebuilt = mapAudios(filePipeline?.load(FileManagerMediaType.Audios)?.files ?: repository.loadAudios())
                changeState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        items = rebuilt,
                        tabs = buildAudioTabs(rebuilt),
                        selectedIds = emptySet(),
                        deletedBytes = outcome.freedBytes,
                    )
                }
            },
            onResult = { changeState { it.copy(phase = FileOperationPhase.Result) } },
            onFailure = { error ->
                changeState { it.copy(phase = FileOperationPhase.Browsing, errorMessage = error.message ?: appString(R.string.deletion_failed)) }
            },
        )
    }

    private fun continueManaging() {
        changeState { it.copy(phase = FileOperationPhase.Browsing, selectedIds = emptySet(), detailStartIndex = null) }
    }
}
