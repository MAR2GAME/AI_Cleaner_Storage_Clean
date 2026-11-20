package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import com.aicleanerstorage.spaceclean.data.whatsapp.loadWhatsAppFileGroups

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionChecker

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.data.whatsapp.WhatsAppCleanerGroup
import com.aicleanerstorage.spaceclean.data.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.data.whatsapp.WhatsAppFileBucket
import com.aicleanerstorage.cleanerkit.files.FileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhatsAppCleanerViewModel(
    private val message: (Int) -> String,
    private val repository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val permissionChecker: PermissionChecker? = null,
) : MviViewModel<WhatsAppCleanerUiState, WhatsAppCleanerAction, WhatsAppCleanerEvent>(WhatsAppCleanerUiState()) {
    private var hasStarted = false
    private var scanJob: Job? = null
    private var cleanJob: Job? = null

    override fun onAction(action: WhatsAppCleanerAction) {
        when (action) {
            is WhatsAppCleanerAction.NavigationRequested -> sendEvent(WhatsAppCleanerEvent.Navigate(action.destination))
            WhatsAppCleanerAction.BackClicked -> { cancelActiveOperation(); sendEvent(WhatsAppCleanerEvent.Exit(false)) }
            WhatsAppCleanerAction.CompletionExitClicked -> sendEvent(WhatsAppCleanerEvent.Exit(true))
            WhatsAppCleanerAction.ScanStartRequested -> startScanIfNeeded()
            WhatsAppCleanerAction.CleanClicked -> cleanSelectedFiles()
            WhatsAppCleanerAction.RetryClicked -> retry()
            WhatsAppCleanerAction.OperationCancellationRequested -> cancelActiveOperation()
            WhatsAppCleanerAction.CleaningCancellationRequested -> cancelCleaningAndReturnToResult()
            WhatsAppCleanerAction.ResultDismissed -> clearResult()
            WhatsAppCleanerAction.ResultRequested -> showResult()
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
                    if (permissionChecker?.isGranted(AppPermission.StorageFiles) == false) {
                        hasStarted = false
                        changeState { it.copy(phase = WhatsAppCleanerPhase.Error, errorMessage = "permission_denied") }
                        return@launch
                    }
                    val buckets = repository.loadWhatsAppFileGroups()
                    delay(SCAN_DELAY_MILLIS)
                    val nextState = buildScanResultState(buckets)
                    setState { nextState }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    changeState {
                        it.copy(
                            phase = WhatsAppCleanerPhase.Error,
                            errorMessage = error.message ?: appString(R.string.whatsapp_clean_unavailable),
                        )
                    }
                }
            }
    }

    private fun toggleGroup(group: WhatsAppCleanerGroup) {
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            val targetGroup = state.groups.firstOrNull { it.group == group } ?: return@changeState state
            val shouldSelect = !targetGroup.selected
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children = groupItem.children.map { child ->
                                if (child.hasFiles) child.copy(selected = shouldSelect) else child
                            },
                        )
                    }
                },
            )
        }
    }

    private fun toggleCategory(group: WhatsAppCleanerGroup, category: WhatsAppCleanerCategory) {
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children = groupItem.children.map { child ->
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
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            state.copy(
                groups = state.groups.map { groupItem ->
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

        val selectedFiles =
            state.groups
                .flatMap { it.children }
                .filter { it.selected }
                .flatMap { it.files }
                .distinctBy { it.path ?: it.uri.toString() }
        val expectedBytes = selectedFiles.sumOf { it.sizeBytes }
        val cleaningStartedAt = System.currentTimeMillis()

        changeState { WhatsAppCleanerReducer.cleaningStarted(it, expectedBytes, selectedFiles.size) }
        cleanJob?.cancel()
        cleanJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    if (permissionChecker?.isGranted(AppPermission.StorageFiles) == false) {
                        changeState { it.copy(phase = WhatsAppCleanerPhase.Error, errorMessage = "permission_denied") }
                        return@launch
                    }
                    val result = repository.deleteFiles(selectedFiles)
                    if (result.deletedCount <= 0) error(appString(R.string.whatsapp_clean_unavailable))
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    setState { WhatsAppCleanerUiState(
                            phase = WhatsAppCleanerPhase.CompleteAnimation,
                            deletedBytes = result.freedBytes,
                            deletedCount = result.deletedCount,
                        ) }
                    delay(COMPLETE_ANIMATION_MILLIS)
                    dispatch(WhatsAppCleanerAction.ResultRequested)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    changeState {
                        it.copy(
                            phase = WhatsAppCleanerPhase.Error,
                            errorMessage = error.message ?: appString(R.string.whatsapp_clean_unavailable),
                        )
                    }
                }
            }
    }

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
        changeState { WhatsAppCleanerReducer.resultRequested(it) }
    }

    private fun cancelCleaningAndReturnToResult() {
        cleanJob?.cancel()
        cleanJob = null
        changeState { WhatsAppCleanerReducer.cleaningCancelled(it) }
    }

    override fun onCleared() {
        scanJob?.cancel()
        cleanJob?.cancel()
        super.onCleared()
    }

    private fun buildScanResultState(buckets: Map<WhatsAppFileBucket, List<ManagedFileItem>>): WhatsAppCleanerUiState {
        val groups =
            WhatsAppCleanerGroup.values().map { group ->
                WhatsAppCleanerGroupItem(
                    group = group,
                    children =
                        WhatsAppCleanerCategory.values().map { category ->
                            val categoryFiles = buckets[WhatsAppFileBucket(group, category)].orEmpty()
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

    private fun appString(resId: Int): String = message(resId)
}

