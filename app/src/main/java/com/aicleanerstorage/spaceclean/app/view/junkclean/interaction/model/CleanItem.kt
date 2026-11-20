package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.model

import com.aicleanerstorage.spaceclean.base.util.FormatUtil

import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel

/**
 * 濞撳懐鎮婃い褰掝暙閸╃喐膩 */
data class CleanItem(
    val junkFile: JunkFileModel,
    val isChecked: Boolean = true,
) {
    val category: JunkCategory get() = junkFile.category
    val fileSize: Long get() = junkFile.fileSize
    val formattedSize: String get() = FormatUtil.formatSize(junkFile.fileSize)
    val fileName: String get() = junkFile.fileName
}
