package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.withSelectionIds

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.appString
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.fileScanFailedMessage
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleAllVisible
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PhotoPrivacyManagerViewModel(
    private val repository: FileRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val removeDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<PhotoPrivacyManagerUiState, FileManagerAction, FileManagerEvent>(PhotoPrivacyManagerUiState()) {
    private val operations = FileOperations(viewModelScope, ioDispatcher) {
        val items = repository.loadPrivacyImages().take(60).withSelectionIds()
        PhotoPrivacyManagerUiState(
            phase = if (items.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
            items = items,
            selectedIds = items.map { it.id }.toSet(),
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

            FileManagerAction.StopDismissed -> dismissStopDialog()
            FileManagerAction.OperationCancellationRequested -> cancelActiveOperation()
            FileManagerAction.InitialLoadRequested -> startIfNeeded()
            FileManagerAction.RefreshRequested -> refresh()
            is FileManagerAction.SelectionToggled -> toggleSelection(intent.id)
            FileManagerAction.VisibleItemsToggled -> toggleVisibleItems()
            is FileManagerAction.VisibleSelectionToggled -> toggleVisibleIds(intent.ids)
            FileManagerAction.LocationRemovalClicked -> requestRemoveLocation()
            FileManagerAction.LocationRemovalDismissed -> cancelRemoveLocation()
            FileManagerAction.ErrorDismissed -> clearError()
            FileManagerAction.LocationRemovalConfirmed -> removeLocationData()
            FileManagerAction.ContinueManagingClicked -> continueManaging()
            else -> Unit
        }
    }

    private fun handleBack() {
        when {
            currentState.phase == FileOperationPhase.Deleting -> showStopDialog()
            currentState.phase == FileOperationPhase.Scanning -> showStopDialog(currentState.phase)
            currentState.phase == FileOperationPhase.ConfirmDelete -> cancelRemoveLocation()
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
    private fun startIfNeeded() {
        if (currentState.phase != FileOperationPhase.Scanning || currentState.items.isNotEmpty()) return
        refresh()
    }

    private fun refresh() {
        operations.scan(
            delayMillis = scanDelayMillis,
            onStart = { setState { PhotoPrivacyManagerUiState(phase = FileOperationPhase.Scanning) } },
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
            state.copy(selectedIds = toggleAllVisible(state.selectedIds, state.visibleIds))
        }
    }

    private fun toggleVisibleIds(ids: Set<Int>) {
        changeState { state ->
            state.copy(selectedIds = toggleIds(state.selectedIds, ids))
        }
    }

    private fun requestRemoveLocation() {
        changeState {
            if (it.selectedIds.isNotEmpty()) {
                it.copy(phase = FileOperationPhase.ConfirmDelete)
            } else {
                it
            }
        }
    }

    private fun cancelRemoveLocation() {
        changeState { it.copy(phase = FileOperationPhase.Browsing) }
    }

    private fun clearError() {
        changeState { it.copy(errorMessage = null) }
    }

    private fun removeLocationData() {
        val selectedFiles = currentState.selectedFiles
        operations.changeFiles(
            hasSelection = selectedFiles.isNotEmpty(),
            onEmptySelection = { changeState { it.copy(phase = FileOperationPhase.Browsing) } },
            onStart = { changeState { it.copy(phase = FileOperationPhase.Deleting) } },
            operationDelayMillis = removeDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.removeLocationData(selectedFiles) },
            isSuccessful = { it > 0 },
            onCompleteAnimation = { outcome, loaded ->
                changeState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        items = loaded.items,
                        selectedIds = emptySet(),
                        removedLocationCount = outcome,
                    )
                }
            },
            onResult = { changeState { it.copy(phase = FileOperationPhase.Result) } },
            onFailure = { error ->
                changeState {
                    it.copy(phase = FileOperationPhase.Browsing, errorMessage = error.message ?: appString(R.string.deletion_failed))
                }
            },
        )
    }

    private fun continueManaging() {
        changeState { it.copy(phase = FileOperationPhase.Browsing, selectedIds = it.items.map { item -> item.id }.toSet()) }
    }
}
