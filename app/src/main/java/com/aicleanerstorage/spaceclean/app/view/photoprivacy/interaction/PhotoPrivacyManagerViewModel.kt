package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toUiText
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleAllVisible
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.withSelectionIds
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PhotoPrivacyManagerViewModel(
    private val repository: ManagedFileRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val removeDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<PhotoPrivacyManagerUiState, PhotoPrivacyAction, FileManagerEvent>(PhotoPrivacyManagerUiState()) {
    private val operations =
        FileOperations(viewModelScope, ioDispatcher) {
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

    override fun onAction(intent: PhotoPrivacyAction) {
        if (exitRequested) return
        when (intent) {
            is PhotoPrivacyAction.NavigationRequested -> sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))
            PhotoPrivacyAction.PermissionRejected -> exit(home = false)
            PhotoPrivacyAction.BackClicked -> handleBack()

            PhotoPrivacyAction.StopDismissed -> dismissStopDialog()
            PhotoPrivacyAction.OperationCancellationRequested -> cancelActiveOperation()
            PhotoPrivacyAction.InitialLoadRequested -> startIfNeeded()
            PhotoPrivacyAction.RefreshRequested -> refresh()
            is PhotoPrivacyAction.SelectionToggled -> toggleSelection(intent.id)
            PhotoPrivacyAction.VisibleItemsToggled -> toggleVisibleItems()
            is PhotoPrivacyAction.VisibleSelectionToggled -> toggleVisibleIds(intent.ids)
            PhotoPrivacyAction.DeleteClicked -> requestRemoveLocation()
            PhotoPrivacyAction.DeleteDismissed -> cancelRemoveLocation()
            PhotoPrivacyAction.DeletePrepared -> removeLocationData()
            PhotoPrivacyAction.LocationRemovalClicked -> requestRemoveLocation()
            PhotoPrivacyAction.LocationRemovalDismissed -> cancelRemoveLocation()
            PhotoPrivacyAction.ErrorDismissed -> clearError()
            PhotoPrivacyAction.LocationRemovalConfirmed -> removeLocationData()
            PhotoPrivacyAction.ContinueManagingClicked -> continueManaging()
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
            onStart = { setState { PhotoPrivacyManagerUiState(phase = FileOperationPhase.Scanning) } },
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
            state.copy(selectedIds = toggleAllVisible(state.selectedIds, state.visibleIds))
        }
    }

    private fun toggleVisibleIds(ids: Set<Int>) {
        setState { state ->
            state.copy(selectedIds = toggleIds(state.selectedIds, ids))
        }
    }

    private fun requestRemoveLocation() {
        setState {
            if (it.selectedIds.isNotEmpty()) {
                it.copy(phase = FileOperationPhase.ConfirmDelete)
            } else {
                it
            }
        }
    }

    private fun cancelRemoveLocation() {
        setState { it.copy(phase = FileOperationPhase.Browsing) }
    }

    private fun clearError() {
        setState { it.copy(error = null) }
    }

    private fun removeLocationData() {
        val selectedFiles = currentState.selectedFiles
        operations.changeFiles(
            hasSelection = selectedFiles.isNotEmpty(),
            onEmptySelection = { setState { it.copy(phase = FileOperationPhase.Browsing) } },
            onStart = { setState { it.copy(phase = FileOperationPhase.Deleting) } },
            operationDelayMillis = removeDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.removeLocationData(selectedFiles) },
            isSuccessful = { (it.getOrNull() ?: 0) > 0 },
            onCompleteAnimation = { outcome, loaded ->
                setState {
                    it.copy(
                        phase = FileOperationPhase.CompleteAnimation,
                        items = loaded.items,
                        selectedIds = emptySet(),
                        removedLocationCount = outcome.getOrNull() ?: 0,
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
        setState { it.copy(phase = FileOperationPhase.Browsing, selectedIds = it.items.map { item -> item.id }.toSet()) }
    }
}
