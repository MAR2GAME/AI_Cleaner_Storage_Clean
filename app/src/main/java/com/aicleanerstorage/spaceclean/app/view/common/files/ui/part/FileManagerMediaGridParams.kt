package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem

/** 媒体网格视图的展示参数与回调。 */
data class FileManagerMediaGridParams(
    val tabs: List<FileManagerTabDisplayItem>,
    val items: List<FileImageDisplayItem>,
    val selectedIds: Set<Int>,
    val scrollState: ScrollState,
    val onToggleVisibleItems: (Set<Int>) -> Unit,
    val onSelect: (Int) -> Unit,
    val onOpenDetail: (FileImageDisplayItem) -> Unit,
    val selectedTabIndex: Int = 0,
    val onTabSelected: (Int) -> Unit = {},
    val showPlayBadge: Boolean = true,
)
