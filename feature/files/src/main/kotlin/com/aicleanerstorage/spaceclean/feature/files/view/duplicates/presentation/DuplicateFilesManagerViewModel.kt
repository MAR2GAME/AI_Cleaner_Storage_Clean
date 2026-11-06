package com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.appString
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.duplicateScanFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.deletionFailedMessage
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleId
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toggleIds
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.feature.files.domain.duplicates.DuplicateFilesPreferences
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DuplicateFilesManagerViewModel(
    private val repository: FileRepository,
    private val preferences: DuplicateFilesPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val testLoader: (((suspend () -> Unit)) -> Unit)? = null,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L
) : MviViewModel<DuplicateFilesManagerUiState, FileManagerIntent, FileManagerEffect>(DuplicateFilesManagerUiState(showWarning = !preferences.isWarningAccepted())) {
    private val featureKey = FeatureKey.DUPLICATE_FILES
    private var activeJob: Job? = null
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

            is DuplicateFilesManagerStateChanged -> updateState { DuplicateFilesManagerReducer.reduce(this, intent) }
            is FileManagerIntent.ShowStopDialog -> showStopDialog(intent.blockedPhase)
            FileManagerIntent.DismissStopDialog -> dismissStopDialog()
            FileManagerIntent.CancelActiveOperation -> cancelActiveOperation()
            FileManagerIntent.CancelDeletingAndReturnToBrowsing -> cancelDeletingAndReturnToBrowsing()
            FileManagerIntent.CompletionAdFinished -> showPendingResult()
            FileManagerIntent.StartIfNeeded -> startIfNeeded()
            FileManagerIntent.Refresh -> refresh()
            is FileManagerIntent.OpenGroup -> openGroup(intent.group)
            FileManagerIntent.CloseGroup -> closeGroup()
            FileManagerIntent.ToggleAll -> toggleAll()
            FileManagerIntent.AcceptWarning -> acceptWarning()
            is FileManagerIntent.ToggleFile -> toggleFile(intent.file)
            FileManagerIntent.AutoSelectCurrentGroup -> autoSelectCurrentGroup()
            is FileManagerIntent.ToggleGroupSelection -> toggleGroupSelection(intent.group)
            FileManagerIntent.ToggleCurrentGroupSelection -> toggleCurrentGroupSelection()
            FileManagerIntent.RequestDelete -> requestDelete()
            FileManagerIntent.RequestDeleteCurrentGroup -> requestDeleteCurrentGroup()
            FileManagerIntent.CancelDelete -> cancelDelete()
            FileManagerIntent.RejectSystemDelete -> rejectSystemDelete()
            FileManagerIntent.ClearError -> clearError()
            FileManagerIntent.ClearSkippedDeleteCount -> clearSkippedDeleteCount()
            FileManagerIntent.DeleteSelectedFiles -> deleteSelectedFiles()
            FileManagerIntent.ContinueManaging -> continueManaging()
            FileManagerIntent.DeleteReady -> deleteSelectedFiles()
            else -> Unit
        }
    }

    private fun handleBack() {
        when {
            currentState.selectedGroup != null -> closeGroup()
            currentState.phase == FileOperationPhase.Deleting -> showStopDialog()
            currentState.phase == FileOperationPhase.Scanning -> showStopDialog(currentState.phase)
            currentState.phase == FileOperationPhase.ConfirmDelete -> cancelDelete()
            else -> exit(FeatureExitReason.Return, home = currentState.phase == FileOperationPhase.Result)
        }
    }
    private fun changeState(transform: (DuplicateFilesManagerUiState) -> DuplicateFilesManagerUiState) {
        synchronized(stateUpdateLock) { dispatch(DuplicateFilesManagerStateChanged(transform(currentState))) }
    }
    private fun publishState(value: DuplicateFilesManagerUiState) {
        synchronized(stateUpdateLock) { dispatch(DuplicateFilesManagerStateChanged(value)) }
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


    constructor(
        repository: FileRepository,
        preferences: DuplicateFilesPreferences,
        ioDispatcher: CoroutineDispatcher
    ) : this(
        repository = repository,
        preferences = preferences,
        ioDispatcher = ioDispatcher,
        testLoader = null,
        scanDelayMillis = 900L,
        deleteDelayMillis = FILE_DELETE_ANIMATION_MIN_MILLIS,
        completeDelayMillis = 800L
    )

    private fun startIfNeeded() {
        if (hasStartedForCurrentState()) return
        refresh()
    }

    private fun refresh() {
        if (activeJob?.isActive == true) return
        trackScanStarted()
        publishState(DuplicateFilesManagerUiState(
                phase = FileOperationPhase.Scanning,
                showWarning = !preferences.isWarningAccepted(),
            ))
        launchOperation {
            runCatching { mapDuplicateGroups(repository.loadDuplicateFiles()) }
                .onSuccess { groups ->
                    delayIfNeeded(scanDelayMillis)
                    trackScanFinished(hasResult = groups.isNotEmpty())
                    publishState(DuplicateFilesManagerUiState(
                        phase = if (groups.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
                        groups = groups,
                        selectedFileKeys = emptySet(),
                        showWarning = !preferences.isWarningAccepted(),
                    ))
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    trackScanFinished(hasResult = false)
                    changeState {
                        it.copy(
                            phase = FileOperationPhase.NoResults,
                            errorMessage = error.message ?: duplicateScanFailedMessage()
                        )
                    }
                }
        }
    }

    private fun openGroup(group: DuplicateGroupItem) {
        changeState { it.copy(selectedGroupId = group.id) }
    }

    private fun closeGroup() {
        changeState { it.copy(selectedGroupId = null) }
    }

    private fun toggleAll() {
        changeState {
            it.copy(selectedFileKeys = toggleIds(it.selectedFileKeys, it.allDeleteFileKeys))
        }
    }

    private fun acceptWarning() {
        preferences.acceptWarning()
        changeState { it.copy(showWarning = false) }
    }

    private fun toggleFile(file: DuplicateFileEntry) {
        val key = duplicateFileKey(file)
        changeState {
            it.copy(selectedFileKeys = toggleId(it.selectedFileKeys, key))
        }
    }

    private fun autoSelectCurrentGroup() {
        val group = currentState.selectedGroup ?: return
        val groupKeys = group.files.map(::duplicateFileKey).toSet()
        val deletableGroupKeys = group.files.drop(1).map(::duplicateFileKey).toSet()
        changeState {
            it.copy(selectedFileKeys = (it.selectedFileKeys - groupKeys) + deletableGroupKeys)
        }
    }

    private fun toggleGroupSelection(group: DuplicateGroupItem) {
        val groupKeys = group.files.map(::duplicateFileKey).toSet()
        val deletableGroupKeys = group.files.drop(1).map(::duplicateFileKey).toSet()
        changeState {
            val selectedGroupKeys = it.selectedFileKeys.intersect(groupKeys)
            it.copy(
                selectedFileKeys = if (selectedGroupKeys == deletableGroupKeys) {
                    it.selectedFileKeys - groupKeys
                } else {
                    (it.selectedFileKeys - groupKeys) + deletableGroupKeys
                }
            )
        }
    }

    private fun toggleCurrentGroupSelection() {
        val group = currentState.selectedGroup ?: return
        toggleGroupSelection(group)
    }

    private fun requestDelete() {
        if (currentState.globalFilesToDelete.isNotEmpty()) {
            changeState { it.copy(phase = FileOperationPhase.ConfirmDelete, pendingDeleteFileKeys = null) }
        }
    }

    private fun requestDeleteCurrentGroup() {
        val state = currentState
        val selectedGroupKeys = state.selectedGroupFileKeys
        if (state.selectedGroupFilesToDelete.isNotEmpty()) {
            changeState {
                it.copy(
                    phase = FileOperationPhase.ConfirmDelete,
                    pendingDeleteFileKeys = selectedGroupKeys
                )
            }
        }
    }

    private fun cancelDelete() {
        changeState { it.copy(phase = FileOperationPhase.Browsing, pendingDeleteFileKeys = null) }
    }

    private fun rejectSystemDelete() {
        cancelDelete()
    }

    private fun clearError() {
        changeState { it.copy(errorMessage = null) }
    }

    private fun clearSkippedDeleteCount() {
        changeState { it.copy(skippedDeleteCount = 0) }
    }

    private fun onCancelDeletingPhase() {
        changeState { current ->
            if (current.phase == FileOperationPhase.Deleting) {
                current.copy(phase = FileOperationPhase.Browsing, pendingDeleteFileKeys = null)
            } else {
                current
            }
        }
    }

    private fun deleteSelectedFiles() {
        val selectedFiles = currentState.filesToDelete
        runFileOperation(
            selectedFiles = selectedFiles,
            action = OperationAction.DELETE,
            onEmptySelection = {
                changeState { it.copy(phase = FileOperationPhase.Browsing, pendingDeleteFileKeys = null) }
            },
            onStart = { changeState { it.copy(phase = FileOperationPhase.Deleting, selectedGroupId = null) } },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = {
                val verifiedFiles = selectedFiles.filter { !it.duplicateHash.isNullOrBlank() && it.duplicatePeers.isNotEmpty() }
                val result = repository.deleteFiles(verifiedFiles)
                val skipped = selectedFiles.size - verifiedFiles.size + result.skippedCount
                changeState { it.copy(skippedDeleteCount = skipped) }
                FileOperationOutcome(freedBytes = result.freedBytes, changedCount = result.deletedCount)
            },
            isSuccessful = { it.changedCount > 0 },
            onCompleteAnimation = { outcome ->
                val groups = mapDuplicateGroups(repository.loadDuplicateFiles())
                changeState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        groups = groups,
                        selectedGroupId = null,
                        selectedFileKeys = emptySet(),
                        pendingDeleteFileKeys = null,
                        deletedBytes = outcome.freedBytes,
                    )
                }
            },
            onResult = { changeState { it.copy(phase = FileOperationPhase.Result) } },
            onFailure = { error ->
                changeState {
                    it.copy(
                        phase = FileOperationPhase.Browsing,
                        pendingDeleteFileKeys = null,
                        errorMessage = error.message ?: appString(R.string.deletion_failed)
                    )
                }
            },
        )
    }

    private fun continueManaging() {
        changeState {
            it.copy(
                phase = FileOperationPhase.Browsing,
                selectedFileKeys = emptySet(),
                pendingDeleteFileKeys = null,
                selectedGroupId = null
            )
        }
    }

    private fun hasStartedForCurrentState(): Boolean {
        val state = currentState
        if (state.phase != FileOperationPhase.Scanning) return true
        return state.groups.isNotEmpty() || state.errorMessage != null
    }
}
