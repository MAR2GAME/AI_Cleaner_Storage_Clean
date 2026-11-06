package com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.formatManagedFileDate


import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem

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
                sizeLabel = first.formattedSize,
                duplicateCount = files.size,
                files = files.mapIndexed { index, file ->
                    DuplicateFileEntry(
                        id = index + 1,
                        path = file.path ?: file.name,
                        date = formatManagedFileDate(file.modifiedSeconds),
                        sizeLabel = file.formattedSize,
                        selected = index > 0,
                        note = if (index == 0) "Removal not recommended" else null,
                        realFile = file
                    )
                }
            )
        }
