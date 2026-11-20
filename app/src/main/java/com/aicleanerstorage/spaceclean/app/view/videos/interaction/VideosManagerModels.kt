package com.aicleanerstorage.spaceclean.app.view.videos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.buildMediaTabs
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterMediaGridItems
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

data class VideoItem(
    val id: Int,
    val file: ManagedFileItemModel,
)

data class VideosManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<VideoItem> = emptyList(),
    val tabs: List<FileManagerTabDisplayItem> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toDisplayItem() }
    val displayTabs: List<FileManagerTabDisplayItem> get() = tabs
    val visibleDisplayItems: List<FileImageDisplayItem>
        get() = filterMediaGridItems(tabs.getOrNull(selectedTabIndex)?.title.orEmpty(), displayItems)
    val visibleIds: Set<Int> get() = visibleDisplayItems.map { it.id }.toSet()
    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
    val selectedFiles: List<ManagedFileItemModel> get() = items.filter { it.id in selectedIds }.map { it.file }
    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }
    val selectedUris: List<FileUri> get() = selectedFiles.map { it.uri }
    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}

fun mapVideos(files: List<ManagedFileItemModel>, limit: Int = 60): List<VideoItem> =
    files.take(limit).mapIndexed { index, file -> VideoItem(id = index + 1, file = file) }

fun buildVideoTabs(items: List<VideoItem>): List<FileManagerTabDisplayItem> {
    val displayItems = items.map { it.toDisplayItem() }
    return buildMediaTabs(
        titles = listOf("All", "DCIM", "Download"),
        items = displayItems,
        fileForId = { id -> items.firstOrNull { it.id == id }?.file },
    )
}

fun VideoItem.toDisplayItem(): FileImageDisplayItem =
    managedFileToImageDisplayItem(id = id, file = file)
