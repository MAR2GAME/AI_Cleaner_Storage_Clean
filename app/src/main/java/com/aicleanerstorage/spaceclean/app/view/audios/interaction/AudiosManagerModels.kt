package com.aicleanerstorage.spaceclean.app.view.audios.interaction

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

data class AudioItem(
    val id: Int,
    val file: ManagedFileItemModel,
)

data class AudiosManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<AudioItem> = emptyList(),
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

fun mapAudios(files: List<ManagedFileItemModel>, limit: Int = 60): List<AudioItem> =
    files.take(limit).mapIndexed { index, file -> AudioItem(id = index + 1, file = file) }

fun buildAudioTabs(items: List<AudioItem>): List<FileManagerTabDisplayItem> {
    val displayItems = items.map { it.toDisplayItem() }
    return buildMediaTabs(
        titles = listOf("All", "Music"),
        items = displayItems,
        fileForId = { id -> items.firstOrNull { it.id == id }?.file },
    )
}

fun AudioItem.toDisplayItem(): FileImageDisplayItem =
    managedFileToImageDisplayItem(id = id, file = file)
