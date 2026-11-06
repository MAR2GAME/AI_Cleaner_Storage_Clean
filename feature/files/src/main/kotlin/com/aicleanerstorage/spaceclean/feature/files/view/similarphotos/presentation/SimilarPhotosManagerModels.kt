package com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter

data class SimilarPhotoItem(
    val id: Int,
    val file: ManagedFileItem,
    val bestPhoto: Boolean = false,
)

data class SimilarPhotoGroup(
    val count: Int,
    val items: List<SimilarPhotoItem>,
)

data class SimilarPhotosManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val groups: List<SimilarPhotoGroup> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
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
    val selectedFiles: List<ManagedFileItem> get() = items.filter { it.id in selectedIds }.map { it.file }
    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }
    val selectedUris: List<FileUri> get() = selectedFiles.map { it.uri }
    val resultSize: Pair<String, String> get() = FileSizeFormatter.format(deletedBytes).splitFileSizeLabel()
}

fun buildSimilarPhotoGroups(files: List<ManagedFileItem>, limit: Int = 60): List<SimilarPhotoGroup> {
    val source = files.take(limit)
    fun key(file: ManagedFileItem): String {
        val day = file.modifiedSeconds / 86_400L
        val sizeBucket = (file.sizeBytes / (512L * 1024L)).coerceAtLeast(0L)
        val bucket = file.bucketName.orEmpty().lowercase()
        return "$bucket#$day#$sizeBucket"
    }

    var nextId = 1
    return source
        .groupBy(::key)
        .values
        .filter { it.size > 1 }
        .take(12)
        .map { group ->
            val sorted = group.sortedByDescending { it.sizeBytes }
            val items = sorted.mapIndexed { index, file ->
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
    managedFileToImageDisplayItem(
        id = id,
        file = file,
        bestPhoto = bestPhoto,
    )
