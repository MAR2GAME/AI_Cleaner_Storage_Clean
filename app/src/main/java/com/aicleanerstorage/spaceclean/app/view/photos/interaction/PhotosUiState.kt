package com.aicleanerstorage.spaceclean.app.view.photos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 照片页状态：公共选择/删除状态 + 固定 itemIds 的分组 tab。
 */
data class PhotosUiState(
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

    val displayTabs: List<FileManagerTabDisplayItem> get() = tabs

    val displayItems: List<FileImageDisplayItem>
        get() = items.map { it.toImageDisplayItem() }

    val currentDisplayItems: List<FileImageDisplayItem>
        get() {
            val ids = tabs.getOrNull(selectedTabIndex)?.itemIds ?: return displayItems
            return displayItems.filter { it.id in ids }
        }
}
