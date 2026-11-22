package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.formatManagedFileDate
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

data class DuplicateFileEntry(
    val id: Int,
    val path: String,
    val date: String,
    val sizeLabel: String,
    val selected: Boolean,
    val note: String? = null,
    val realFile: ManagedFileModel? = null,
)

data class DuplicateGroupItem(
    val id: Int,
    val name: String,
    val sizeLabel: String,
    val duplicateCount: Int,
    val files: List<DuplicateFileEntry>,
    val group: DuplicateFileGroupModel,
)

fun duplicateFileKey(file: DuplicateFileEntry): String = file.realFile?.uri ?: "${file.path}#${file.date}#${file.sizeLabel}"

fun mapDuplicateGroups(groups: List<DuplicateFileGroupModel>): List<DuplicateGroupItem> =
    groups
        .filter { it.files.isNotEmpty() }
        .mapIndexed { groupIndex, group ->
            val first = group.files.first()
            DuplicateGroupItem(
                id = groupIndex + 1,
                name = first.name,
                sizeLabel = FormatUtil.formatSize(first.sizeBytes),
                duplicateCount = group.files.size,
                group = group,
                files =
                    group.files.mapIndexed { index, file ->
                        DuplicateFileEntry(
                            id = index + 1,
                            path = file.path ?: file.name,
                            date = formatManagedFileDate(file.modifiedSeconds),
                            sizeLabel = FormatUtil.formatSize(file.sizeBytes),
                            selected = index > 0,
                            note = if (index == 0) "Removal not recommended" else null,
                            realFile = file,
                        )
                    },
            )
        }
