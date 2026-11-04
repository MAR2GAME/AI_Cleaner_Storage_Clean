package com.aicleanerstorage.spaceclean.feature.junkclean.presentation.model

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile

/**
 * 濞撳懐鎮婃い褰掝暙閸╃喐膩 */
data class CleanItem(
    val junkFile: JunkFile,
    val isChecked: Boolean = true,
) {
    val category: JunkCategory get() = junkFile.category
    val fileSize: Long get() = junkFile.fileSize
    val formattedSize: String get() = junkFile.formattedSize
    val fileName: String get() = junkFile.fileName
}
