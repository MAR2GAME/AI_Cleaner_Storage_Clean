package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.openDetailIndex
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toUiText
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.port.SimilarPhotoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class SimilarPhotosManagerViewModel(
    private val repository: SimilarPhotoRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<SimilarPhotosManagerUiState, SimilarPhotosAction, FileManagerEvent>(SimilarPhotosManagerUiState()) {
    private val operations =
        FileOperations(viewModelScope, ioDispatcher) {
            val groups = mapSimilarPhotoGroups(repository.loadGroups())
            SimilarPhotosManagerUiState(
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

    override fun onAction(intent: SimilarPhotosAction) {
        if (exitRequested) return
        when (intent) {
            is SimilarPhotosAction.NavigationRequested -> sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))
            SimilarPhotosAction.PermissionRejected -> exit(home = false)
            SimilarPhotosAction.BackClicked -> handleBack()
            SimilarPhotosAction.DetailBackClicked -> {
                closeDetail()
                sendEvent(FileManagerEvent.NavigateBack)
            }

            SimilarPhotosAction.StopDismissed -> dismissStopDialog()
            SimilarPhotosAction.OperationCancellationRequested -> cancelActiveOperation()
            SimilarPhotosAction.InitialLoadRequested -> startIfNeeded()
            SimilarPhotosAction.RefreshRequested -> refresh()
            is SimilarPhotosAction.SelectionToggled -> toggleSelection(intent.id)
            SimilarPhotosAction.VisibleItemsToggled -> toggleVisibleItems()
            is SimilarPhotosAction.GroupToggled -> toggleGroup(intent.ids)
            is SimilarPhotosAction.GroupItemsToggled -> toggleGroup(intent.ids)
            is SimilarPhotosAction.DetailClicked -> {
                openDetail(intent.index)
                sendEvent(FileManagerEvent.Navigate(AppDestination.SimilarPhotosDetail(intent.index), reset = false))
            }
            SimilarPhotosAction.DeleteClicked -> requestDelete()
            SimilarPhotosAction.DeleteDismissed -> cancelDelete()
            SimilarPhotosAction.ErrorDismissed -> clearError()
            SimilarPhotosAction.DeleteConfirmed -> deleteSelectedFiles()
            SimilarPhotosAction.ContinueManagingClicked -> continueManaging()
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
        if (currentState.phase != FileOperationPhase.Scanning || currentState.groups.isNotEmpty()) return
        refresh()
    }

    private fun refresh() {
        operations.scan(
            delayMillis = scanDelayMillis,
            onStart = { setState { SimilarPhotosManagerUiState(phase = FileOperationPhase.Scanning) } },
            onLoaded = { loaded -> setState { loaded } },
            onFailure = { error ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.NoResults,
                        error = error.toFileOperationError(FileOperationError.ScanFailed).toUiText(),
                    )
                }
            },
        )
    }

    private fun toggleSelection(id: Int) {
        setState { it.copy(selectedIds = toggleId(it.selectedIds, id)) }
    }

    private fun toggleVisibleItems() {
        setState { state ->
            state.copy(selectedIds = toggleIds(state.selectedIds, state.bulkSelectableIds))
        }
    }

    private fun toggleGroup(ids: Set<Int>) {
        setState { it.copy(selectedIds = toggleIds(it.selectedIds, ids)) }
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
            operation = { repository.delete(selectedFiles).getOrThrow() },
            isSuccessful = { it.deletedCount > 0 },
            onCompleteAnimation = { outcome, loaded ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        groups = loaded.groups,
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
                        error = error.toFileOperationError(FileOperationError.DeletionFailed).toUiText(),
                    )
                }
            },
        )
    }

    private fun continueManaging() {
        setState { it.copy(phase = FileOperationPhase.Browsing, selectedIds = emptySet(), detailStartIndex = null) }
    }
}
