package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileDetailDisplayItem


import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri

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
