package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.model

import com.aicleanerstorage.spaceclean.base.util.FormatUtil

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory

data class CategoryCleanGroup(
    val category: JunkCategory,
    val items: List<CleanItem>,
) {
    val totalSize: Long
        get() = items.sumOf { it.fileSize }

    val checkedSize: Long
        get() = items.filter { it.isChecked }.sumOf { it.fileSize }

    val checkedCount: Int
        get() = items.count { it.isChecked }

    val totalCount: Int
        get() = items.size

    val formattedTotalSize: String
        get() = FormatUtil.formatSize(totalSize)
}
