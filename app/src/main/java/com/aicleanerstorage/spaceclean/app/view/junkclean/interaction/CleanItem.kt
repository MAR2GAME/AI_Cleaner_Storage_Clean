package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile

/** 选中项：领域 JunkFile + 本次会话的勾选状态。 */
data class CleanItem(
    val junkFile: JunkFile,
    val isChecked: Boolean = true,
) {
    val category: JunkCategory get() = junkFile.category
    val fileSize: Long get() = junkFile.fileSize
    val formattedSize: String get() = junkFile.formattedSize
    val fileName: String get() = junkFile.fileName
}
