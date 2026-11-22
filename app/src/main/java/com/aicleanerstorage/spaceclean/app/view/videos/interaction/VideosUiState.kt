package com.aicleanerstorage.spaceclean.app.view.videos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterMediaGridItems
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 视频页状态：公共选择/删除状态 + 视频 tab。
 */
data class VideosUiState(
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

    val displayItems: List<FileImageDisplayItem>
        get() = items.map { it.toImageDisplayItem() }

    val visibleDisplayItems: List<FileImageDisplayItem>
        get() = filterMediaGridItems(tabs.getOrNull(selectedTabIndex)?.title.orEmpty(), displayItems)
}
