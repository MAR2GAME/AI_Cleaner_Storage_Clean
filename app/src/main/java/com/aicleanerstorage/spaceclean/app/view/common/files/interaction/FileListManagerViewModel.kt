package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toUiText
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FileListManagerViewModel(
    private val repository: FileRepository,
    private val loadFiles: suspend FileRepository.() -> List<ManagedFileItem>,
    private val iconKind: FileListIconKind,
    private val detailDestination: (Int) -> AppDestination,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<FileListManagerUiState, FileManagerAction, FileManagerEvent>(FileListManagerUiState(iconKind = iconKind)) {
    private val operations =
        FileOperations(viewModelScope, ioDispatcher) {
            val files = repository.loadFiles().sortedByDescending { it.sizeBytes }.withSelectionIds()
            FileListManagerUiState(
                iconKind = iconKind,
                phase = if (files.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
                items = files,
                tabs = buildListTabs(files, iconKind),
            )
        }

    private var exitRequested = false

    private fun exit(home: Boolean) {
        if (exitRequested) return
        exitRequested = true
        operations.cancel()
        sendEvent(FileManagerEvent.Exit(home))
    }

    override fun onAction(intent: FileManagerAction) {
        if (exitRequested) return
        when (intent) {
            is FileManagerAction.NavigationRequested -> sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))
            FileManagerAction.PermissionRejected -> exit(home = false)
            FileManagerAction.BackClicked -> handleBack()
            FileManagerAction.DetailBackClicked -> {
                closeDetail()
                sendEvent(FileManagerEvent.NavigateBack)
            }

            FileManagerAction.StopDismissed -> dismissStopDialog()
            FileManagerAction.OperationCancellationRequested -> cancelActiveOperation()
            FileManagerAction.InitialLoadRequested -> startIfNeeded()
            FileManagerAction.RefreshRequested -> refresh()
            is FileManagerAction.TabChangeRequested -> selectTab(intent.index)
            is FileManagerAction.SelectionToggled -> toggleSelection(intent.id)
            FileManagerAction.VisibleItemsToggled -> toggleVisibleItems()
            is FileManagerAction.DetailClicked -> {
                openDetail(intent.index)
                sendEvent(FileManagerEvent.Navigate(detailDestination(intent.index), reset = false))
            }
            FileManagerAction.DeleteClicked -> requestDelete()
            FileManagerAction.DeleteDismissed -> cancelDelete()
            FileManagerAction.ErrorDismissed -> clearError()
            FileManagerAction.DeleteConfirmed -> deleteSelectedFiles()
            FileManagerAction.ContinueManagingClicked -> continueManaging()
            else -> Unit
        }
    }

    private fun handleBack() {
        when {
            currentState.phase == FileOperationPhase.Deleting -> showStopDialog()
            currentState.phase == FileOperationPhase.Scanning -> showStopDialog(currentState.phase)
            currentState.phase == FileOperationPhase.ConfirmDelete -> cancelDelete()
            else -> exit(home = true)
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
        if (currentState.phase != FileOperationPhase.Scanning || currentState.items.isNotEmpty()) return
        refresh()
    }

    private fun refresh() {
        operations.scan(
            delayMillis = scanDelayMillis,
            onStart = { setState { FileListManagerUiState(iconKind = iconKind, phase = FileOperationPhase.Scanning) } },
            onLoaded = { loaded -> setState { loaded } },
            onFailure = { error ->
                setState { it.copy(phase = FileOperationPhase.NoResults, error = FileOperationError.ScanFailed.toUiText()) }
            },
        )
    }

    private fun selectTab(index: Int) {
        setState {
            it.copy(
                selectedTabIndex = index.coerceAtLeast(0),
                selectedIds = emptySet(),
                detailStartIndex = null,
            )
        }
    }

    private fun toggleSelection(id: Int) {
        setState {
            it.copy(selectedIds = toggleId(it.selectedIds, id))
        }
    }

    private fun toggleVisibleItems() {
        setState { state ->
            state.copy(selectedIds = toggleAllVisible(state.selectedIds, state.visibleIds))
        }
    }

    private fun openDetail(index: Int?) {
        setState { it.copy(detailStartIndex = openDetailIndex(index)) }
    }

    private fun closeDetail() {
        setState { it.copy(detailStartIndex = null) }
    }

    private fun requestDelete() {
        setState {
            if (it.selectedIds.isNotEmpty()) {
                it.copy(phase = FileOperationPhase.ConfirmDelete)
            } else {
                it
            }
        }
    }

    private fun cancelDelete() {
        setState { it.copy(phase = FileOperationPhase.Browsing) }
    }

    private fun clearError() {
        setState { it.copy(error = null) }
    }

    private fun deleteSelectedFiles() {
        val selectedFiles = currentState.selectedFiles
        operations.changeFiles(
            hasSelection = selectedFiles.isNotEmpty(),
            onEmptySelection = { setState { it.copy(phase = FileOperationPhase.Browsing) } },
            onStart = { setState { it.copy(phase = FileOperationPhase.Deleting, detailStartIndex = null) } },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.deleteFiles(selectedFiles) },
            isSuccessful = { it.deletedCount > 0 },
            onCompleteAnimation = { outcome, loaded ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        items = loaded.items,
                        tabs = loaded.tabs,
                        selectedIds = emptySet(),
                        deletedBytes = outcome.freedBytes,
                    )
                }
            },
            onResult = { setState { it.copy(phase = FileOperationPhase.Result) } },
            onFailure = { error ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.Browsing,
                        error = FileOperationError.DeletionFailed.toUiText(),
                    )
                }
            },
        )
    }

    private fun continueManaging() {
        setState { it.copy(phase = FileOperationPhase.Browsing, selectedIds = emptySet(), detailStartIndex = null) }
    }
}
