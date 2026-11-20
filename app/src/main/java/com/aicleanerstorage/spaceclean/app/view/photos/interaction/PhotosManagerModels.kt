package com.aicleanerstorage.spaceclean.app.view.photos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.totalSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

data class PhotosManagerTab(
    val title: String,
    val sizeLabel: String?,
    val itemIds: Set<Int>,
)

data class PhotosManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<IndexedFileItem> = emptyList(),
    val tabs: List<PhotosManagerTab> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toImageDisplayItem() }

    val displayTabs: List<FileManagerTabDisplayItem>
        get() = tabs.map { FileManagerTabDisplayItem(it.title, it.sizeLabel.orEmpty()) }

    val currentDisplayItems: List<FileImageDisplayItem>
        get() {
            val ids = tabs.getOrNull(selectedTabIndex)?.itemIds ?: items.map { it.id }.toSet()
            return displayItems.filter { it.id in ids }
        }

    val visibleIds: Set<Int> get() = currentDisplayItems.map { it.id }.toSet()

    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)

    val selectedFiles: List<ManagedFileItem>
        get() = items.filter { it.id in selectedIds }.map { it.file }

    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }


    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}

fun buildPhotoTabs(items: List<IndexedFileItem>): List<PhotosManagerTab> {
    fun IndexedFileItem.matchesFolder(folder: String): Boolean =
        file.bucketName?.contains(folder, ignoreCase = true) == true ||
            file.path?.contains("/$folder/", ignoreCase = true) == true

    val pictures = items.filter { it.matchesFolder("Pictures") }
    val dcim = items.filter { it.matchesFolder("DCIM") || it.matchesFolder("Camera") }
    val used = (pictures + dcim).map { it.id }.toSet()
    val other = items.filter { it.id !in used }

    fun tab(title: String, tabItems: List<IndexedFileItem>) =
        PhotosManagerTab(
            title = title,
            sizeLabel = totalSizeLabel(tabItems.map { it.file }).takeIf { it.isNotBlank() },
            itemIds = tabItems.map { it.id }.toSet(),
        )

    return listOf(
        tab("Photo", items),
        tab("Pictures", pictures),
        tab("DCIM", dcim),
        tab("Other", other),
    )
}
