package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerGroup
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import com.aicleanerstorage.spaceclean.domain.port.WhatsAppCleanerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhatsAppCleanerViewModel(
    private val repository: WhatsAppCleanerRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<WhatsAppCleanerUiState, WhatsAppCleanerAction, WhatsAppCleanerEvent>(WhatsAppCleanerUiState()) {
    private var hasStarted = false
    private var scanJob: Job? = null
    private var cleanJob: Job? = null

    override fun onAction(action: WhatsAppCleanerAction) {
        when (action) {
            is WhatsAppCleanerAction.NavigationRequested -> sendEvent(WhatsAppCleanerEvent.Navigate(action.destination))
            WhatsAppCleanerAction.BackClicked -> {
                cancelActiveOperation()
                sendEvent(WhatsAppCleanerEvent.Exit(false))
            }
            WhatsAppCleanerAction.CompletionExitClicked -> sendEvent(WhatsAppCleanerEvent.Exit(true))
            WhatsAppCleanerAction.ScanStartRequested -> startScanIfNeeded()
            WhatsAppCleanerAction.CleanClicked -> cleanSelectedFiles()
            WhatsAppCleanerAction.RetryClicked -> retry()
            WhatsAppCleanerAction.OperationCancellationRequested -> cancelActiveOperation()
            WhatsAppCleanerAction.CleaningCancellationRequested -> cancelCleaningAndReturnToResult()
            WhatsAppCleanerAction.ResultDismissed -> clearResult()
            WhatsAppCleanerAction.ResultRequested -> showResult()
            WhatsAppCleanerAction.PermissionDenied ->
                setState {
                    it.copy(
                        phase = WhatsAppCleanerPhase.Error,
                        error = UiText.Res(R.string.permission_denied),
                    )
                }
            is WhatsAppCleanerAction.GroupToggled -> toggleGroup(action.group)
            is WhatsAppCleanerAction.CategoryToggled -> toggleCategory(action.group, action.category)
            is WhatsAppCleanerAction.GroupExpansionToggled -> toggleExpanded(action.group)
        }
    }

    private fun startScanIfNeeded() {
        if (hasStarted) return
        hasStarted = true
        scanJob?.cancel()
        setState { WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.Scanning) }
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    val buckets = repository.loadGroups()
                    delay(SCAN_DELAY_MILLIS)
                    val nextState = buildScanResultState(buckets)
                    setState { nextState }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    setState {
                        it.copy(
                            phase = WhatsAppCleanerPhase.Error,
                            error = UiText.Res(R.string.whatsapp_clean_unavailable),
                        )
                    }
                }
            }
    }

    private fun toggleGroup(group: WhatsAppCleanerGroup) {
        setState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@setState state
            val targetGroup = state.groups.firstOrNull { it.group == group } ?: return@setState state
            val shouldSelect = !targetGroup.selected
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children =
                                groupItem.children.map { child ->
                                    if (child.hasFiles) child.copy(selected = shouldSelect) else child
                                },
                        )
                    }
                },
            )
        }
    }

    private fun toggleCategory(
        group: WhatsAppCleanerGroup,
        category: WhatsAppCleanerCategory,
    ) {
        setState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@setState state
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children =
                                groupItem.children.map { child ->
                                    if (child.category == category && child.hasFiles) {
                                        child.copy(selected = !child.selected)
                                    } else {
                                        child
                                    }
                                },
                        )
                    }
                },
            )
        }
    }

    private fun toggleExpanded(group: WhatsAppCleanerGroup) {
        setState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@setState state
            state.copy(
                groups =
                    state.groups.map { groupItem ->
                        if (groupItem.group == group) {
                            groupItem.copy(expanded = !groupItem.expanded)
                        } else {
                            groupItem
                        }
                    },
            )
        }
    }

    private fun cleanSelectedFiles() {
        val state = currentState
        if (state.phase != WhatsAppCleanerPhase.ScanResult || state.selectedBytes <= 0L) return

        val selectedFiles = selectedWhatsAppFiles(state)
        val expectedBytes = selectedFiles.sumOf { it.sizeBytes }
        val cleaningStartedAt = System.currentTimeMillis()
        setState { WhatsAppCleanerReducer.cleaningStarted(it, expectedBytes, selectedFiles.size) }
        cleanJob?.cancel()
        cleanJob = viewModelScope.launch(ioDispatcher) { runCleaning(selectedFiles, cleaningStartedAt) }
    }

    private suspend fun runCleaning(
        selectedFiles: List<ManagedFileModel>,
        cleaningStartedAt: Long,
    ) {
        try {
            val result = repository.delete(selectedFiles).getOrThrow()
            if (result.deletedCount <= 0) error("WhatsApp clean unavailable")
            delayRemainingCleaningAnimation(cleaningStartedAt)
            setState {
                WhatsAppCleanerUiState(
                    phase = WhatsAppCleanerPhase.CompleteAnimation,
                    deletedBytes = result.freedBytes,
                    deletedCount = result.deletedCount,
                )
            }
            delay(COMPLETE_ANIMATION_MILLIS)
            dispatch(WhatsAppCleanerAction.ResultRequested)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            setState {
                it.copy(
                    phase = WhatsAppCleanerPhase.Error,
                    error = UiText.Res(R.string.whatsapp_clean_unavailable),
                )
            }
        }
    }

    private fun selectedWhatsAppFiles(state: WhatsAppCleanerUiState): List<ManagedFileModel> =
        state.groups
            .flatMap { it.children }
            .filter { it.selected }
            .flatMap { it.files }
            .distinctBy { it.path ?: it.uri }

    private suspend fun delayRemainingCleaningAnimation(startedAtMillis: Long) {
        val remainingMillis = CLEANING_ANIMATION_MIN_MILLIS - (System.currentTimeMillis() - startedAtMillis)
        if (remainingMillis > 0L) delay(remainingMillis)
    }

    private fun retry() {
        cancelActiveOperation()
        hasStarted = false
        startScanIfNeeded()
    }

    private fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        cleanJob?.cancel()
        cleanJob = null
        hasStarted = false
    }

    private fun clearResult() {
        cancelActiveOperation()
        setState { WhatsAppCleanerUiState() }
    }

    private fun showResult() {
        setState { WhatsAppCleanerReducer.resultRequested(it) }
    }

    private fun cancelCleaningAndReturnToResult() {
        cleanJob?.cancel()
        cleanJob = null
        setState { WhatsAppCleanerReducer.cleaningCancelled(it) }
    }

    override fun onCleared() {
        scanJob?.cancel()
        cleanJob?.cancel()
        super.onCleared()
    }

    private fun buildScanResultState(buckets: Map<WhatsAppFileBucketModel, List<ManagedFileModel>>): WhatsAppCleanerUiState {
        val groups =
            WhatsAppCleanerGroup.values().map { group ->
                WhatsAppCleanerGroupItem(
                    group = group,
                    children =
                        WhatsAppCleanerCategory.values().map { category ->
                            val categoryFiles = buckets[WhatsAppFileBucketModel(group, category)].orEmpty()
                            WhatsAppCleanerSubItem(
                                group = group,
                                category = category,
                                files = categoryFiles,
                                selected = categoryFiles.isNotEmpty() && category.shouldSelectByDefault(group),
                            )
                        },
                )
            }
        return WhatsAppCleanerUiState(
            phase = WhatsAppCleanerPhase.ScanResult,
            groups = groups,
            scannedBytes = groups.sumOf { it.totalBytes },
            selectedBytes = groups.sumOf { it.selectedBytes },
            selectedCount = groups.flatMap { it.children }.filter { it.selected }.sumOf { it.files.size },
        )
    }

    private fun WhatsAppCleanerUiState.withGroups(newGroups: List<WhatsAppCleanerGroupItem>): WhatsAppCleanerUiState =
        copy(
            groups = newGroups,
            selectedBytes = newGroups.sumOf { it.selectedBytes },
            selectedCount = newGroups.flatMap { it.children }.filter { it.selected }.sumOf { it.files.size },
        )

    private fun WhatsAppCleanerCategory.shouldSelectByDefault(group: WhatsAppCleanerGroup): Boolean =
        group == WhatsAppCleanerGroup.Cache || this == WhatsAppCleanerCategory.Other

    private companion object {
        private const val SCAN_DELAY_MILLIS = 650L
        private const val CLEANING_ANIMATION_MIN_MILLIS = 1600L
        private const val COMPLETE_ANIMATION_MILLIS = 800L
    }
}
