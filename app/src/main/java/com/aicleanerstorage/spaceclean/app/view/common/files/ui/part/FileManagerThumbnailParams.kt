package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.ui.Modifier
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem

/** 文件缩略图的展示参数与回调。 */
data class FileManagerThumbnailParams(
    val item: FileImageDisplayItem,
    val selected: Boolean,
    val onOpen: () -> Unit,
    val onToggleSelection: () -> Unit,
    val showPlayBadge: Boolean = false,
    val modifier: Modifier = Modifier,
)
