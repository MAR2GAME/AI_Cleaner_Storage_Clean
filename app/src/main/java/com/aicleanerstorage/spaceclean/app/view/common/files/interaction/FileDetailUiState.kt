package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailDisplayItem

data class FileDetailUiState(
    val items: List<FileDetailDisplayItem> = emptyList(),
    val initialIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val selectedSizeBytes: Long = 0L,
    val confirmDeleteVisible: Boolean = false,
) {
    val selectedCount: Int get() = selectedIds.size
    val totalCount: Int get() = items.size
}
