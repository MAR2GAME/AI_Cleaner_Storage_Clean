package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

data class FileListManagerUiState(
    val iconKind: FileListIconKind,
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<IndexedFileItem> = emptyList(),
    val tabs: List<FileManagerTabDisplayItem> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileListDisplayItem> get() = items.map { managedFileToListDisplayItem(it.id, it.file, iconKind) }

    val visibleDisplayItems: List<FileListDisplayItem>
        get() = filterFileManagerListItems(tabs.getOrNull(selectedTabIndex)?.title.orEmpty(), displayItems)

    val visibleIds: Set<Int> get() = visibleDisplayItems.map { it.id }.toSet()

    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)

    val selectedFiles: List<ManagedFileItem>
        get() = items.filter { it.id in selectedIds }.map { it.file }

    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }

    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}
