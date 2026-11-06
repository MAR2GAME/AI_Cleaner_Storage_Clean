package com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListIconKind
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.managedFileToListDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.buildListTabs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.filterFileManagerListItems
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter

data class LargeFileItem(
    val id: Int,
    val file: ManagedFileItem,
)

data class LargeFilesManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<LargeFileItem> = emptyList(),
    val tabs: List<FileManagerTabDisplayItem> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileListDisplayItem> get() = items.map { it.toDisplayItem() }

    val displayTabs: List<FileManagerTabDisplayItem> get() = tabs

    val visibleDisplayItems: List<FileListDisplayItem>
        get() = filterFileManagerListItems(tabs.getOrNull(selectedTabIndex)?.title.orEmpty(), displayItems)

    val visibleIds: Set<Int> get() = visibleDisplayItems.map { it.id }.toSet()

    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)

    val selectedFiles: List<ManagedFileItem>
        get() = items.filter { it.id in selectedIds }.map { it.file }

    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }

    val selectedUris: List<FileUri> get() = selectedFiles.map { it.uri }

    val resultSize: Pair<String, String> get() = FileSizeFormatter.format(deletedBytes).splitFileSizeLabel()
}

fun mapLargeFiles(files: List<ManagedFileItem>): List<LargeFileItem> =
    files.sortedByDescending { it.sizeBytes }
        .mapIndexed { index, file -> LargeFileItem(id = index + 1, file = file) }

fun buildLargeFileTabs(items: List<LargeFileItem>): List<FileManagerTabDisplayItem> {
    val displayItems = items.map { it.toDisplayItem() }
    return buildListTabs(
        titles = listOf("All", "Download", "Other"),
        items = displayItems,
        fileForId = { id -> items.firstOrNull { it.id == id }?.file },
    )
}

fun LargeFileItem.toDisplayItem(): FileListDisplayItem =
    managedFileToListDisplayItem(
        id = id,
        file = file,
        iconKind = FileListIconKind.LargeVideo,
    )

val LargeFilesListStyle: FileListDisplayStyle = FileListDisplayStyle.Default
