package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileModelToImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

data class SimilarPhotoItem(
    val id: Int,
    val file: ManagedFileModel,
    val bestPhoto: Boolean = false,
)

data class SimilarPhotoGroup(
    val count: Int,
    val items: List<SimilarPhotoItem>,
)

data class SimilarPhotosManagerUiState(
    val permissionGranted: Boolean = false,
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val groups: List<SimilarPhotoGroup> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val error: UiText? = null,
) {
    val items: List<SimilarPhotoItem> get() = groups.flatMap { it.items }
    val totalPhotoCount: Int get() = items.size
    val totalSizeBytes: Long get() = items.sumOf { it.file.sizeBytes }
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toDisplayItem() }
    val displayGroups: List<FileImageGroupDisplayItem>
        get() = groups.map { group -> FileImageGroupDisplayItem(group.count, group.items.map { it.toDisplayItem() }) }
    val visibleIds: Set<Int> get() = items.map { it.id }.toSet()
    val bulkSelectableIds: Set<Int> get() = items.filterNot { it.bestPhoto }.map { it.id }.toSet()
    val allSelected: Boolean get() = bulkSelectableIds.isNotEmpty() && selectedIds.containsAll(bulkSelectableIds)
    val selectedFiles: List<ManagedFileModel> get() = items.filter { it.id in selectedIds }.map { it.file }
    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }
    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}

internal fun mapSimilarPhotoGroups(groups: List<List<ManagedFileModel>>): List<SimilarPhotoGroup> {
    var nextId = 1
    return groups.map { group ->
        val items =
            group.mapIndexed { index, file ->
                SimilarPhotoItem(
                    id = nextId++,
                    file = file,
                    bestPhoto = index == 0,
                )
            }
        SimilarPhotoGroup(count = items.size, items = items)
    }
}

fun SimilarPhotoItem.toDisplayItem(): FileImageDisplayItem =
    managedFileModelToImageDisplayItem(
        id = id,
        file = file,
        bestPhoto = bestPhoto,
    )
