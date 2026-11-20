package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailDisplayItem


import com.aicleanerstorage.spaceclean.domain.model.FileUri

data class FileDetailUiState(
    val items: List<FileDetailDisplayItem> = emptyList(),
    val initialIndex: Int = 0,
    val selectedIds: Set<Int> = emptySet(),
    val selectedSizeBytes: Long = 0L,
    val selectedUris: List<FileUri> = emptyList(),
    val confirmDeleteVisible: Boolean = false,
) {
    val selectedCount: Int get() = selectedIds.size
    val totalCount: Int get() = items.size
}
