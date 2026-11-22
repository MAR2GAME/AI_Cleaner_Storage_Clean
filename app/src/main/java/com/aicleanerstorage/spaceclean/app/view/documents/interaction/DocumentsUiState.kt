package com.aicleanerstorage.spaceclean.app.view.documents.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListIconKind
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterFileManagerListItems
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToListDisplayItem
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/** 文档页状态：公共选择/删除状态 + 列表投影 + All/Download/Other tab。 */
data class DocumentsUiState(
    val page: FilePageState = FilePageState(),
    val permissionGranted: Boolean = false,
    val tabs: List<FileManagerTabDisplayItem> = emptyList(),
    val selectedTabIndex: Int = 0,
) {
    val phase: FileOperationPhase get() = page.phase
    val items: List<IndexedFileItem> get() = page.items
    val selectedIds: Set<Int> get() = page.selectedIds
    val detailStartIndex: Int? get() = page.detailStartIndex
    val deletedBytes: Long get() = page.deletedBytes
    val error: UiText? get() = page.error
    val showStopDialog: Boolean get() = page.showStopDialog
    val blockedPhase: FileOperationPhase? get() = page.blockedPhase
    val selectedFiles: List<ManagedFileModel> get() = page.selectedFiles
    val selectedSizeBytes: Long get() = page.selectedSizeBytes
    val resultSize: Pair<String, String> get() = page.resultSize

    val listItems: List<FileListDisplayItem>
        get() = items.map { managedFileToListDisplayItem(it.id, it.file, FileListIconKind.Document) }

    val visibleListItems: List<FileListDisplayItem>
        get() = filterFileManagerListItems(tabs.getOrNull(selectedTabIndex)?.title.orEmpty(), listItems)

    val visibleIds: Set<Int> get() = visibleListItems.map { it.id }.toSet()

    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
}
