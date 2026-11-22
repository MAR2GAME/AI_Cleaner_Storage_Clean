package com.aicleanerstorage.spaceclean.app.view.screenshots.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 截图页状态：无 tab，直接展示全部截图。
 */
data class ScreenshotsUiState(
    val page: FilePageState = FilePageState(),
    val permissionGranted: Boolean = false,
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

    val allSelected: Boolean
        get() = items.isNotEmpty() && selectedIds.containsAll(items.map { it.id })
}
