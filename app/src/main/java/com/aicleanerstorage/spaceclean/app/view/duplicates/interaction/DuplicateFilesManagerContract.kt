package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.base.mvi.UiText

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
        get() = groups.flatMap { group -> group.files.drop(1).map(::duplicateFileKey) }.toSet()

    val activeDeleteFileKeys: Set<String>
        get() = pendingDeleteFileKeys ?: selectedFileKeys

    val filesToDelete: List<ManagedFileItem>
        get() = filesMatchingKeys(activeDeleteFileKeys)

    val globalFilesToDelete: List<ManagedFileItem>
        get() = filesMatchingKeys(selectedFileKeys)

    val selectedGroupFileKeys: Set<String>
        get() {
            val group = selectedGroup ?: return emptySet()
            val groupKeys = group.files.map(::duplicateFileKey).toSet()
            return selectedFileKeys.intersect(groupKeys)
        }

    val selectedGroupFilesToDelete: List<ManagedFileItem>
        get() = filesMatchingKeys(selectedGroupFileKeys)

    val selectedGroupDeleteSize: Long get() = selectedGroupFilesToDelete.sumOf { it.sizeBytes }

    private fun filesMatchingKeys(keys: Set<String>): List<ManagedFileItem> =
        groups.flatMap { group ->
            group.files
                .filter { duplicateFileKey(it) in keys }
                .mapNotNull { it.realFile }
        }

    val selectedDeleteSize: Long get() = filesToDelete.sumOf { it.sizeBytes }

    val allSelected: Boolean
        get() = allDeleteFileKeys.isNotEmpty() && selectedFileKeys.containsAll(allDeleteFileKeys)
}
