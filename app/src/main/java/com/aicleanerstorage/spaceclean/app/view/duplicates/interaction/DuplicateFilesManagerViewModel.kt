package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toUiText
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DuplicateFilesManagerViewModel(
    private val repository: ManagedFileRepository,
    private val settingsRepository: AppSettingsRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<DuplicateFilesManagerUiState, DuplicateFilesAction, FileManagerEvent>(
        DuplicateFilesManagerUiState(showWarning = !settingsRepository.isDuplicateFilesWarningAccepted()),
    ) {
    private val operations =
        FileOperations(viewModelScope, ioDispatcher) {
            val groups = mapDuplicateGroups(repository.loadDuplicateGroups())
            DuplicateFilesManagerUiState(
                phase = if (groups.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
                groups = groups,
            )
        }

    private var exitRequested = false

    private fun exit(home: Boolean) {
        if (exitRequested) return
        exitRequested = true
        operations.cancel()
        sendEvent(FileManagerEvent.Exit(home))
    }

    override fun onAction(intent: DuplicateFilesAction) {
        if (exitRequested) return
        when (intent) {
            is DuplicateFilesAction.NavigationRequested -> sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))
            DuplicateFilesAction.PermissionRejected -> exit(home = false)
            DuplicateFilesAction.BackClicked -> handleBack()

            DuplicateFilesAction.StopDismissed -> dismissStopDialog()
            DuplicateFilesAction.OperationCancellationRequested -> cancelActiveOperation()
            DuplicateFilesAction.InitialLoadRequested -> startIfNeeded()
            DuplicateFilesAction.RefreshRequested -> refresh()
            is DuplicateFilesAction.GroupClicked -> openGroup(intent.group)
            DuplicateFilesAction.SelectAllClicked -> toggleAll()
            DuplicateFilesAction.WarningConfirmed -> acceptDuplicateFilesWarning()
            is DuplicateFilesAction.FileToggled -> toggleFile(intent.file)
            DuplicateFilesAction.AutoSelectClicked -> autoSelectCurrentGroup()
            is DuplicateFilesAction.GroupSelectionToggled -> toggleGroupSelection(intent.group)
            DuplicateFilesAction.DeleteClicked -> requestDelete()
            DuplicateFilesAction.GroupDeleteClicked -> requestDeleteCurrentGroup()
            DuplicateFilesAction.DeleteDismissed -> cancelDelete()
            DuplicateFilesAction.ErrorDismissed -> clearError()
            DuplicateFilesAction.SkippedDeleteCountConsumed -> clearSkippedDeleteCount()
            DuplicateFilesAction.DeleteConfirmed -> deleteSelectedFiles()
            DuplicateFilesAction.ContinueManagingClicked -> continueManaging()
        }
    }

    private fun handleBack() {
        when {
            currentState.selectedGroup != null -> closeGroup()
            currentState.phase == FileOperationPhase.Deleting -> showStopDialog()
            currentState.phase == FileOperationPhase.Scanning -> showStopDialog(currentState.phase)
            currentState.phase == FileOperationPhase.ConfirmDelete -> cancelDelete()
            else -> exit(home = currentState.phase == FileOperationPhase.Result)
        }
    }

    private fun showStopDialog(blockedPhase: FileOperationPhase? = null) {
        setState { it.copy(showStopDialog = true, blockedPhase = blockedPhase) }
    }

    private fun dismissStopDialog() {
        setState { it.copy(showStopDialog = false, blockedPhase = null) }
    }

    private fun cancelActiveOperation() {
        operations.cancel()
        dismissStopDialog()
    }

    private fun startIfNeeded() {
        if (hasStartedForCurrentState()) return
        refresh()
    }

    private fun refresh() {
        operations.scan(
            delayMillis = scanDelayMillis,
            onStart = { setState { DuplicateFilesManagerUiState(showWarning = !settingsRepository.isDuplicateFilesWarningAccepted()) } },
            onLoaded = { loaded -> setState { loaded.copy(showWarning = !settingsRepository.isDuplicateFilesWarningAccepted()) } },
            onFailure = { error ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.NoResults,
                        error = error.toFileOperationError(FileOperationError.DuplicateScanFailed).toUiText(),
                    )
                }
            },
        )
    }

    private fun openGroup(group: DuplicateGroupItem) {
        setState { it.copy(selectedGroupId = group.id) }
    }

    private fun closeGroup() {
        setState { it.copy(selectedGroupId = null) }
    }

    private fun toggleAll() {
        setState {
            it.copy(selectedFileKeys = toggleIds(it.selectedFileKeys, it.allDeleteFileKeys))
        }
    }

    private fun acceptDuplicateFilesWarning() {
        settingsRepository.acceptDuplicateFilesWarning()
        setState { it.copy(showWarning = false) }
    }

    private fun toggleFile(file: DuplicateFileEntry) {
        val key = duplicateFileKey(file)
        setState {
            it.copy(selectedFileKeys = toggleId(it.selectedFileKeys, key))
        }
    }

    private fun autoSelectCurrentGroup() {
        val group = currentState.selectedGroup ?: return
        val groupKeys = group.files.map(::duplicateFileKey).toSet()
        val deletableGroupKeys =
            group.files
                .drop(1)
                .map(::duplicateFileKey)
                .toSet()
        setState {
            it.copy(selectedFileKeys = (it.selectedFileKeys - groupKeys) + deletableGroupKeys)
        }
    }

    private fun toggleGroupSelection(group: DuplicateGroupItem) {
        val groupKeys = group.files.map(::duplicateFileKey).toSet()
        val deletableGroupKeys =
            group.files
                .drop(1)
                .map(::duplicateFileKey)
                .toSet()
        setState {
            val selectedGroupKeys = it.selectedFileKeys.intersect(groupKeys)
            it.copy(
                selectedFileKeys =
                    if (selectedGroupKeys == deletableGroupKeys) {
                        it.selectedFileKeys - groupKeys
                    } else {
                        (it.selectedFileKeys - groupKeys) + deletableGroupKeys
                    },
            )
        }
    }

    private fun requestDelete() {
        if (currentState.globalFilesToDelete.isNotEmpty()) {
            setState { it.copy(phase = FileOperationPhase.ConfirmDelete, pendingDeleteFileKeys = null) }
        }
    }

    private fun requestDeleteCurrentGroup() {
        val state = currentState
        val selectedGroupKeys = state.selectedGroupFileKeys
        if (state.selectedGroupFilesToDelete.isNotEmpty()) {
            setState {
                it.copy(
                    phase = FileOperationPhase.ConfirmDelete,
                    pendingDeleteFileKeys = selectedGroupKeys,
                )
            }
        }
    }

    private fun cancelDelete() {
        setState { it.copy(phase = FileOperationPhase.Browsing, pendingDeleteFileKeys = null) }
    }

    private fun clearError() {
        setState { it.copy(error = null) }
    }

    private fun clearSkippedDeleteCount() {
        setState { it.copy(skippedDeleteCount = 0) }
    }

    private fun deleteSelectedFiles() {
        val selections = currentState.filesToDelete
        operations.changeFiles(
            hasSelection = selections.isNotEmpty(),
            onEmptySelection = {
                setState { it.copy(phase = FileOperationPhase.Browsing, pendingDeleteFileKeys = null) }
            },
            onStart = { setState { it.copy(phase = FileOperationPhase.Deleting, selectedGroupId = null) } },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.deleteDuplicates(selections) },
            isSuccessful = { result ->
                val summary = result.getOrNull()
                setState { it.copy(skippedDeleteCount = summary?.skippedCount ?: 0) }
                result.isSuccess && (summary?.deletedCount ?: 0) > 0
            },
            onCompleteAnimation = { outcome, loaded ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        groups = loaded.groups,
                        selectedGroupId = null,
                        selectedFileKeys = emptySet(),
                        pendingDeleteFileKeys = null,
                        deletedBytes = outcome.getOrNull()?.freedBytes ?: 0L,
                    )
                }
            },
            onResult = { setState { it.copy(phase = FileOperationPhase.Result) } },
            onFailure = { error ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.Browsing,
                        pendingDeleteFileKeys = null,
                        error = error.toFileOperationError(FileOperationError.DeletionFailed).toUiText(),
                    )
                }
            },
        )
    }

    private fun continueManaging() {
        setState {
            it.copy(
                phase = FileOperationPhase.Browsing,
                selectedFileKeys = emptySet(),
                pendingDeleteFileKeys = null,
                selectedGroupId = null,
            )
        }
    }

    private fun hasStartedForCurrentState(): Boolean {
        val state = currentState
        if (state.phase != FileOperationPhase.Scanning) return true
        return state.groups.isNotEmpty() || state.error != null
    }
}
