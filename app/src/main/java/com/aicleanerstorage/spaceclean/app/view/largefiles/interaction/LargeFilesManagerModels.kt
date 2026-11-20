package com.aicleanerstorage.spaceclean.app.view.largefiles.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListIconKind
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.buildListTabs
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterFileManagerListItems
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

data class LargeFileItem(
    val id: Int,
    val file: ManagedFileItemModel,
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

    val selectedFiles: List<ManagedFileItemModel>
        get() = items.filter { it.id in selectedIds }.map { it.file }

    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }

    val selectedUris: List<FileUri> get() = selectedFiles.map { it.uri }

    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}

fun mapLargeFiles(files: List<ManagedFileItemModel>): List<LargeFileItem> =
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
