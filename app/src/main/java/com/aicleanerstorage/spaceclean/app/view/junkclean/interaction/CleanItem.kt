package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel

/** 选中项：领域 JunkFileModel + 本次会话的勾选状态。 */
data class CleanItem(
    val file: JunkFileModel,
    val isChecked: Boolean = true,
) {
    val category: JunkCategory get() = file.category
    val sizeBytes: Long get() = file.sizeBytes
    val formattedSize: String get() = FormatUtil.formatSize(sizeBytes)
    val name: String get() = file.name
}
