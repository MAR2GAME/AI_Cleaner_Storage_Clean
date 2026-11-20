package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel

data class DuplicateFilesManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val groups: List<DuplicateGroupItem> = emptyList(),
    val selectedGroupId: Int? = null,
    val selectedFileKeys: Set<String> = emptySet(),
    val pendingDeleteFileKeys: Set<String>? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
    val showWarning: Boolean = false,
    val skippedDeleteCount: Int = 0,
) {
    val selectedGroup: DuplicateGroupItem?
        get() = groups.firstOrNull { it.id == selectedGroupId }

    val allDeleteFileKeys: Set<String>
        get() = groups.flatMap { group -> group.files.drop(1).map(::duplicateFileKey) }.toSet()

    val activeDeleteFileKeys: Set<String>
        get() = pendingDeleteFileKeys ?: selectedFileKeys

    val filesToDelete: List<ManagedFileItemModel>
        get() = filesMatchingKeys(activeDeleteFileKeys)

    val globalFilesToDelete: List<ManagedFileItemModel>
        get() = filesMatchingKeys(selectedFileKeys)

    val selectedGroupFileKeys: Set<String>
        get() {
            val group = selectedGroup ?: return emptySet()
            val groupKeys = group.files.map(::duplicateFileKey).toSet()
            return selectedFileKeys.intersect(groupKeys)
        }

    val selectedGroupFilesToDelete: List<ManagedFileItemModel>
        get() = filesMatchingKeys(selectedGroupFileKeys)

    val selectedGroupDeleteSize: Long get() = selectedGroupFilesToDelete.sumOf { it.sizeBytes }

    private fun filesMatchingKeys(keys: Set<String>): List<ManagedFileItemModel> =
        groups.flatMap { group ->
            group.files
                .filter { duplicateFileKey(it) in keys }
                .mapNotNull { it.realFile }
        }

    val selectedDeleteSize: Long get() = filesToDelete.sumOf { it.sizeBytes }

    val allSelected: Boolean
        get() = allDeleteFileKeys.isNotEmpty() && selectedFileKeys.containsAll(allDeleteFileKeys)

    val selectedUris: List<FileUri> get() = filesToDelete.map { it.uri }
}
