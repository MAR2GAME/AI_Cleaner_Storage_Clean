package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem

/** 图库浏览器视图的展示参数与回调。 */
data class FileManagerGalleryParams(
    val tabs: List<FileManagerTabDisplayItem>,
    val selectedTabIndex: Int,
    val items: List<FileImageDisplayItem>,
    val selectedIds: Set<Int>,
    val scrollState: ScrollState,
    val onTabSelected: (Int) -> Unit,
    val onSelect: (Int) -> Unit,
    val onSelectAll: () -> Unit,
    val onOpenDetail: (FileImageDisplayItem) -> Unit,
)
