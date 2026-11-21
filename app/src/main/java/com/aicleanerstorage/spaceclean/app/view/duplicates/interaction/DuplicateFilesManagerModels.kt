package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.base.util.FormatUtil

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.formatManagedFileDate


import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem

data class DuplicateFileEntry(
    val id: Int,
    val path: String,
    val date: String,
    val sizeLabel: String,
    val selected: Boolean,
    val note: String? = null,
    val realFile: ManagedFileItem? = null
)

data class DuplicateGroupItem(
    val id: Int,
    val name: String,
    val sizeLabel: String,
    val duplicateCount: Int,
    val files: List<DuplicateFileEntry>
)

fun duplicateFileKey(file: DuplicateFileEntry): String =
    file.realFile?.uri?.toString() ?: "${file.path}#${file.date}#${file.sizeLabel}"

fun mapDuplicateGroups(groups: List<List<ManagedFileItem>>): List<DuplicateGroupItem> =
    groups.filter { it.isNotEmpty() }
        .mapIndexed { groupIndex, files ->
            val first = files.first()
            DuplicateGroupItem(
                id = groupIndex + 1,
                name = first.name,
                sizeLabel = FormatUtil.formatSize(first.sizeBytes),
                duplicateCount = files.size,
                files = files.mapIndexed { index, file ->
                    DuplicateFileEntry(
                        id = index + 1,
                        path = file.path ?: file.name,
                        date = formatManagedFileDate(file.modifiedSeconds),
                        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
                        selected = index > 0,
                        note = if (index == 0) "Removal not recommended" else null,
                        realFile = file
                    )
                }
            )
        }
