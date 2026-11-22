package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageGroupDisplayItem

/** 相似照片视图的展示参数与回调。 */
data class FileManagerSimilarPhotosParams(
    val groups: List<FileImageGroupDisplayItem>,
    val totalCount: Int,
    val totalSizeBytes: Long,
    val selectedIds: Set<Int>,
    val allSelected: Boolean,
    val scrollState: ScrollState,
    val onToggleAll: () -> Unit,
    val onToggleGroup: (FileImageGroupDisplayItem) -> Unit,
    val onSelect: (Int) -> Unit,
    val onOpenDetail: (FileImageDisplayItem) -> Unit,
)
