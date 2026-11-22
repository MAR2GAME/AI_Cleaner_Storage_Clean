package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem

/** 截图网格视图的展示参数与回调。 */
data class FileManagerScreenshotGridParams(
    val items: List<FileImageDisplayItem>,
    val allSelected: Boolean,
    val selectedIds: Set<Int>,
    val scrollState: ScrollState,
    val onToggleAll: () -> Unit,
    val onSelect: (Int) -> Unit,
    val onOpenDetail: (FileImageDisplayItem) -> Unit,
)
