package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDateGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PrivacyPhotoItem(
    val id: Int,
    val file: ManagedFileItemModel,
)

data class PrivacyPhotoGroup(
    val title: String,
    val items: List<PrivacyPhotoItem>,
) {
    val ids: Set<Int> get() = items.map { it.id }.toSet()
}

data class PhotoPrivacyManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<PrivacyPhotoItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val removedLocationCount: Int = 0,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toDisplayItem() }
    val groups: List<PrivacyPhotoGroup> get() = buildPrivacyPhotoGroups(items)
    val displayGroups: List<FileImageDateGroupDisplayItem>
        get() = groups.map { group ->
            FileImageDateGroupDisplayItem(
                title = group.title,
                items = group.items.map { it.toDisplayItem() },
            )
        }
    val visibleIds: Set<Int> get() = items.map { it.id }.toSet()
    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
    val selectedFiles: List<ManagedFileItemModel> get() = items.filter { it.id in selectedIds }.map { it.file }
}

fun mapPrivacyPhotos(files: List<ManagedFileItemModel>, limit: Int = 60): List<PrivacyPhotoItem> =
    files.take(limit).mapIndexed { index, file -> PrivacyPhotoItem(id = index + 1, file = file) }

fun buildPrivacyPhotoGroups(items: List<PrivacyPhotoItem>): List<PrivacyPhotoGroup> {
    val monthFormatter = SimpleDateFormat("yyyy-MM", Locale.US)
    return items
        .groupBy { item -> monthFormatter.format(Date(item.file.modifiedSeconds.toTimestampMillis())) }
        .map { (title, groupItems) -> PrivacyPhotoGroup(title = title, items = groupItems) }
        .sortedByDescending { it.title }
}

private fun Long.toTimestampMillis(): Long =
    if (this > 9_999_999_999L) this else this * 1000L

fun PrivacyPhotoItem.toDisplayItem(): FileImageDisplayItem =
    managedFileToImageDisplayItem(id = id, file = file)
