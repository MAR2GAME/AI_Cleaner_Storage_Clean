package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem

/** 音频列表视图的展示参数与回调。 */
data class FileManagerAudioListParams(
    val tabs: List<FileManagerTabDisplayItem>,
    val items: List<FileImageDisplayItem>,
    val selectedIds: Set<Int>,
    val scrollState: ScrollState,
    val onToggleVisibleItems: (Set<Int>) -> Unit,
    val onSelect: (Int) -> Unit,
    val onOpenDetail: (FileImageDisplayItem) -> Unit,
    val selectedTabIndex: Int = 0,
    val onTabSelected: (Int) -> Unit = {},
)
