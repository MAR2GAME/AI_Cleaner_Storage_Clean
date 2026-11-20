package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.appString
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.fileScanFailedMessage
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.openDetailIndex
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class SimilarPhotosManagerViewModel(
    private val repository: FileRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<SimilarPhotosManagerUiState, FileManagerAction, FileManagerEvent>(SimilarPhotosManagerUiState()) {
    private val operations = FileOperations(viewModelScope, ioDispatcher) {
        val groups = buildSimilarPhotoGroups(repository.loadImages())
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

    override fun onAction(intent: FileManagerAction) {
        if (exitRequested) return
        when (intent) {
            is FileManagerAction.NavigationRequested -> sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))
            FileManagerAction.PermissionRejected -> exit(home = false)
            FileManagerAction.BackClicked -> handleBack()
            FileManagerAction.DetailBackClicked -> { closeDetail(); sendEvent(FileManagerEvent.NavigateBack) }

            is FileManagerAction.StopClicked -> showStopDialog(intent.blockedPhase)
            FileManagerAction.StopDismissed -> dismissStopDialog()
            FileManagerAction.OperationCancellationRequested -> cancelActiveOperation()
            FileManagerAction.DeleteCancellationRequested -> cancelDeletingAndReturnToBrowsing()
            FileManagerAction.InitialLoadRequested -> startIfNeeded()
            FileManagerAction.RefreshRequested -> refresh()
            is FileManagerAction.SelectionToggled -> toggleSelection(intent.id)
            FileManagerAction.VisibleItemsToggled -> toggleVisibleItems()
            is FileManagerAction.GroupToggled -> toggleGroup(intent.ids)
            is FileManagerAction.DetailClicked -> {
                openDetail(intent.index)
                sendEvent(FileManagerEvent.Navigate(AppDestination.SimilarPhotosDetail(intent.index), reset = false))
            }
            FileManagerAction.DetailDismissed -> closeDetail()
            FileManagerAction.DeleteClicked -> requestDelete()
            FileManagerAction.DeleteDismissed -> cancelDelete()
            FileManagerAction.SystemDeleteRejected -> rejectSystemDelete()
            FileManagerAction.ErrorDismissed -> clearError()
            FileManagerAction.DeleteConfirmed -> deleteSelectedFiles()
            FileManagerAction.ContinueManagingClicked -> continueManaging()
            FileManagerAction.DeletePrepared -> deleteSelectedFiles()
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
        changeState { it.copy(showStopDialog = true, blockedPhase = blockedPhase) }
    }
    private fun dismissStopDialog() { changeState { it.copy(showStopDialog = false, blockedPhase = null) } }
    private fun cancelActiveOperation() {
        operations.cancel()
        dismissStopDialog()
    }
    private fun cancelDeletingAndReturnToBrowsing() { cancelActiveOperation(); onCancelDeletingPhase() }
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
                setState { copy(phase = FileOperationPhase.NoResults, errorMessage = error.message ?: fileScanFailedMessage()) }
            },
        )
    }

    private fun toggleSelection(id: Int) {
        changeState { it.copy(selectedIds = toggleId(it.selectedIds, id)) }
    }

    private fun toggleVisibleItems() {
        changeState { state ->
            state.copy(selectedIds = toggleIds(state.selectedIds, state.bulkSelectableIds))
        }
    }

    private fun toggleGroup(ids: Set<Int>) {
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
        operations.changeFiles(
            hasSelection = selectedFiles.isNotEmpty(),
            onEmptySelection = { changeState { it.copy(phase = FileOperationPhase.Browsing) } },
            onStart = { changeState { it.copy(phase = FileOperationPhase.Deleting, detailStartIndex = null) } },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.deleteFiles(selectedFiles) },
            isSuccessful = { it.deletedCount > 0 },
            onCompleteAnimation = { outcome, loaded ->
                changeState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        groups = loaded.groups,
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
