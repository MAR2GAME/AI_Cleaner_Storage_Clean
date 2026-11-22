package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory

data class CategoryCleanGroup(
    val category: JunkCategory,
    val items: List<CleanItem>,
) {
    val totalSize: Long
        get() = items.sumOf { it.sizeBytes }

    val checkedSize: Long
        get() = items.filter { it.isChecked }.sumOf { it.sizeBytes }

    val checkedCount: Int
        get() = items.count { it.isChecked }

    val totalCount: Int
        get() = items.size

    val formattedTotalSize: String
        get() = FormatUtil.formatSize(totalSize)

    val formattedCheckedSize: String
        get() = FormatUtil.formatSize(checkedSize)
}
