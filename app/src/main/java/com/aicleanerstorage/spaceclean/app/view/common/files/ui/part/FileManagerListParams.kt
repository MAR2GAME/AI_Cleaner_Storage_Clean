package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ScrollState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem

/** 文件列表视图的展示参数与回调。 */
data class FileManagerListParams(
    val tabs: List<FileManagerTabDisplayItem>,
    val items: List<FileListDisplayItem>,
    val selectedTabIndex: Int,
    val selectedIds: Set<Int>,
    val allSelected: Boolean,
    val scrollState: ScrollState,
    val style: FileListDisplayStyle,
    val onTabSelected: (Int) -> Unit,
    val onToggleAll: () -> Unit,
    val onSelect: (Int) -> Unit,
    @DrawableRes val leadingIconRes: Int? = null,
    val onOpenDetail: (FileListDisplayItem) -> Unit = {},
)
