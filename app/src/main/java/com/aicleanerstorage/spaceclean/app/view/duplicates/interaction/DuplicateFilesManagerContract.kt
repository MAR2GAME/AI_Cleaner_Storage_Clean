package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel

data class DuplicateFilesManagerUiState(
    val permissionGranted: Boolean = false,
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val groups: List<DuplicateGroupItem> = emptyList(),
    val selectedGroupId: Int? = null,
    val selectedFileKeys: Set<String> = emptySet(),
    val pendingDeleteFileKeys: Set<String>? = null,
    val deletedBytes: Long = 0L,
    val error: UiText? = null,
    val showWarning: Boolean = false,
    val skippedDeleteCount: Int = 0,
) {
    val selectedGroup: DuplicateGroupItem?
        get() = groups.firstOrNull { it.id == selectedGroupId }

    val allDeleteFileKeys: Set<String>
        get() = groups.filter { it.group.hash != null }.flatMap { group -> group.files.drop(1).map(::duplicateFileKey) }.toSet()

    val activeDeleteFileKeys: Set<String>
        get() = pendingDeleteFileKeys ?: selectedFileKeys

    val filesToDelete: List<DuplicateFileSelectionModel>
        get() = filesMatchingKeys(activeDeleteFileKeys)

    val globalFilesToDelete: List<DuplicateFileSelectionModel>
        get() = filesMatchingKeys(selectedFileKeys)

    val selectedGroupFileKeys: Set<String>
        get() {
            val group = selectedGroup ?: return emptySet()
            val files = if (group.group.hash == null) group.files.drop(1) else group.files
            val groupKeys = files.map(::duplicateFileKey).toSet()
            return selectedFileKeys.intersect(groupKeys)
        }

    val selectedGroupFilesToDelete: List<DuplicateFileSelectionModel>
        get() = filesMatchingKeys(selectedGroupFileKeys)

    val selectedGroupDeleteSize: Long get() = selectedGroupFilesToDelete.sumOf { it.file.sizeBytes }

    private fun filesMatchingKeys(keys: Set<String>): List<DuplicateFileSelectionModel> =
        groups.flatMap { group ->
            val files = if (group.group.hash == null) group.files.drop(1) else group.files
            files
                .filter { duplicateFileKey(it) in keys }
                .mapNotNull { entry ->
                    entry.realFile?.let { DuplicateFileSelectionModel(it, group.group) }
                }
        }

    val selectedDeleteSize: Long get() = filesToDelete.sumOf { it.file.sizeBytes }

    val allSelected: Boolean
        get() = allDeleteFileKeys.isNotEmpty() && selectedFileKeys.containsAll(allDeleteFileKeys)
}
