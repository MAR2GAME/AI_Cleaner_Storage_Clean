package com.aicleanerstorage.spaceclean.app.view.largefiles.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FILE_DELETE_ANIMATION_MIN_MILLIS
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListIconKind
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperations
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.buildListTabs
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.openDetailIndex
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileOperationError
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toUiText
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleId
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toggleIds
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.withSelectionIds
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 大文件页 ViewModel。
 *
 * 只负责大文件的加载、tab、选择和删除状态；异步流程复用 [FileOperations]。
 *
 * @param repository 大文件查询与删除端口。
 * @param ioDispatcher 扫描/删除使用的调度器。
 */
class LargeFilesManagerViewModel(
    private val repository: ManagedFileRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scanDelayMillis: Long = 900L,
    private val deleteDelayMillis: Long = FILE_DELETE_ANIMATION_MIN_MILLIS,
    private val completeDelayMillis: Long = 800L,
) : MviViewModel<LargeFilesUiState, FilePageAction, FileManagerEvent>(LargeFilesUiState()) {
    private val operations =
        FileOperations<LargeFilesSnapshot>(viewModelScope, ioDispatcher) {
            LargeFilesSnapshot(repository.loadLargeFiles().sortedByDescending { it.sizeBytes }.withSelectionIds())
        }

    private var exitRequested = false

    override fun onAction(intent: FilePageAction) {
        if (exitRequested) return
        when (intent) {
            is FilePageAction.NavigationRequested ->
                sendEvent(FileManagerEvent.Navigate(intent.destination, intent.reset))

            FilePageAction.PermissionRejected -> exit(home = false)
            FilePageAction.BackClicked -> handleBack()
            FilePageAction.DetailBackClicked -> {
                closeDetail()
                sendEvent(FileManagerEvent.NavigateBack)
            }
            FilePageAction.StopDismissed -> dismissStopDialog()
            FilePageAction.OperationCancellationRequested -> cancelActiveOperation()
            FilePageAction.InitialLoadRequested -> startIfNeeded()
            FilePageAction.RefreshRequested -> refresh()
            is FilePageAction.TabChangeRequested -> selectTab(intent.index)
            is FilePageAction.SelectionToggled -> toggleSelection(intent.id)
            FilePageAction.VisibleItemsToggled -> toggleVisibleItems()
            is FilePageAction.VisibleSelectionToggled -> toggleVisibleItems(intent.ids)
            is FilePageAction.DetailClicked -> {
                openDetail(intent.index)
                sendEvent(FileManagerEvent.Navigate(AppDestination.LargeFilesDetail(intent.index), reset = false))
            }
            FilePageAction.DeleteClicked -> requestDelete()
            FilePageAction.DeleteDismissed -> cancelDelete()
            FilePageAction.ErrorDismissed -> clearError()
            FilePageAction.DeleteConfirmed -> deleteSelectedFiles()
            FilePageAction.ContinueManagingClicked -> continueManaging()
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
        setState { it.copy(page = it.page.copy(showStopDialog = true, blockedPhase = blockedPhase)) }
    }

    private fun dismissStopDialog() {
        setState { it.copy(page = it.page.copy(showStopDialog = false, blockedPhase = null)) }
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
            onStart = {
                setState {
                    it.copy(
                        page = FilePageState(phase = FileOperationPhase.Scanning),
                        tabs = emptyList(),
                        selectedTabIndex = 0,
                    )
                }
            },
            onLoaded = { snapshot ->
                setState {
                    it.copy(
                        page =
                            FilePageState(
                                phase = if (snapshot.items.isEmpty()) FileOperationPhase.NoResults else FileOperationPhase.Browsing,
                                items = snapshot.items,
                            ),
                        tabs = buildListTabs(snapshot.items, FileListIconKind.LargeVideo),
                    )
                }
            },
            onFailure = { error ->
                setState {
                    it.copy(
                        page =
                            it.page.copy(
                                phase = FileOperationPhase.NoResults,
                                error = error.toFileOperationError(FileOperationError.ScanFailed).toUiText(),
                            ),
                    )
                }
            },
        )
    }

    private fun selectTab(index: Int) {
        setState {
            it.copy(
                selectedTabIndex = index.coerceAtLeast(0),
                page = it.page.copy(selectedIds = emptySet(), detailStartIndex = null),
            )
        }
    }

    private fun toggleSelection(id: Int) {
        setState { it.copy(page = it.page.copy(selectedIds = toggleId(it.page.selectedIds, id))) }
    }

    private fun toggleVisibleItems(ids: Set<Int>? = null) {
        setState {
            val visibleIds = ids ?: it.visibleIds
            it.copy(page = it.page.copy(selectedIds = toggleIds(it.page.selectedIds, visibleIds)))
        }
    }

    private fun openDetail(index: Int?) {
        setState { it.copy(page = it.page.copy(detailStartIndex = openDetailIndex(index))) }
    }

    private fun closeDetail() {
        setState { it.copy(page = it.page.copy(detailStartIndex = null)) }
    }

    private fun requestDelete() {
        setState {
            if (it.page.selectedIds.isNotEmpty()) {
                it.copy(page = it.page.copy(phase = FileOperationPhase.ConfirmDelete))
            } else {
                it
            }
        }
    }

    private fun cancelDelete() {
        setState { it.copy(page = it.page.copy(phase = FileOperationPhase.Browsing)) }
    }

    private fun clearError() {
        setState { it.copy(page = it.page.copy(error = null)) }
    }

    private fun deleteSelectedFiles() {
        val selectedFiles = currentState.page.selectedFiles
        operations.changeFiles(
            hasSelection = selectedFiles.isNotEmpty(),
            onEmptySelection = { setState { it.copy(page = it.page.copy(phase = FileOperationPhase.Browsing)) } },
            onStart = {
                setState { it.copy(page = it.page.copy(phase = FileOperationPhase.Deleting, detailStartIndex = null)) }
            },
            operationDelayMillis = deleteDelayMillis,
            completeDelayMillis = completeDelayMillis,
            operation = { repository.delete(selectedFiles) },
            isSuccessful = { (it.getOrNull()?.deletedCount ?: 0) > 0 },
            onCompleteAnimation = { outcome, snapshot ->
                setState {
                    it.copy(
                        page =
                            it.page.copy(
                                phase = FileOperationPhase.CompleteAnimation,
                                items = snapshot.items,
                                selectedIds = emptySet(),
                                detailStartIndex = null,
                                deletedBytes = outcome.getOrNull()?.freedBytes ?: 0L,
                            ),
                        tabs = buildListTabs(snapshot.items, FileListIconKind.LargeVideo),
                    )
                }
            },
            onResult = { setState { it.copy(page = it.page.copy(phase = FileOperationPhase.Result)) } },
            onFailure = { error ->
                setState {
                    it.copy(
                        page =
                            it.page.copy(
                                phase = FileOperationPhase.Browsing,
                                error = error.toFileOperationError(FileOperationError.DeletionFailed).toUiText(),
                            ),
                    )
                }
            },
        )
    }

    private fun continueManaging() {
        setState {
            it.copy(page = it.page.copy(phase = FileOperationPhase.Browsing, selectedIds = emptySet(), detailStartIndex = null))
        }
    }

    private fun exit(home: Boolean) {
        if (exitRequested) return
        exitRequested = true
        operations.cancel()
        sendEvent(FileManagerEvent.Exit(home))
    }

    private data class LargeFilesSnapshot(
        val items: List<IndexedFileItem>,
    )
}
