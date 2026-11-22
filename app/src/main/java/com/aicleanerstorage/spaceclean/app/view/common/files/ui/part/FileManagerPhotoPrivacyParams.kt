package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDateGroupDisplayItem

/** 照片隐私视图的展示参数与回调。 */
data class FileManagerPhotoPrivacyParams(
    val groups: List<FileImageDateGroupDisplayItem>,
    val selectedIds: Set<Int>,
    val allSelected: Boolean,
    val onToggleAll: () -> Unit,
    val onToggleGroup: (Set<Int>) -> Unit,
    val onSelect: (Int) -> Unit,
)
