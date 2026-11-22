package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 扁平文件页的公共状态。
 *
 * 只放六个文件页完全一致的字段；tab、网格/列表投影由各页面自己的
 * UiState 组合并计算。
 */
data class FilePageState(
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<IndexedFileItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val error: UiText? = null,
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
) {
    val selectedFiles: List<ManagedFileModel>
        get() = items.filter { it.id in selectedIds }.map { it.file }

    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }

    val resultSize: Pair<String, String>
        get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}
