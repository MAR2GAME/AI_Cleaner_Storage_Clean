package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDateGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.IndexedFileItem
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PrivacyPhotoGroup(
    val title: String,
    val items: List<IndexedFileItem>,
) {
    val ids: Set<Int> get() = items.map { it.id }.toSet()
}

data class PhotoPrivacyManagerUiState(
    val permissionGranted: Boolean = false,
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<IndexedFileItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val removedLocationCount: Int = 0,
    val error: UiText? = null,
) {
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toImageDisplayItem() }
    val groups: List<PrivacyPhotoGroup> get() = buildPrivacyPhotoGroups(items)
    val displayGroups: List<FileImageDateGroupDisplayItem>
        get() =
            groups.map { group ->
                FileImageDateGroupDisplayItem(
                    title = group.title,
                    items = group.items.map { it.toImageDisplayItem() },
                )
            }
    val visibleIds: Set<Int> get() = items.map { it.id }.toSet()
    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
    val selectedFiles: List<ManagedFileModel> get() = items.filter { it.id in selectedIds }.map { it.file }
}

fun buildPrivacyPhotoGroups(items: List<IndexedFileItem>): List<PrivacyPhotoGroup> {
    val monthFormatter = SimpleDateFormat("yyyy-MM", Locale.US)
    return items
        .groupBy { item -> monthFormatter.format(Date(item.file.modifiedSeconds.toTimestampMillis())) }
        .map { (title, groupItems) -> PrivacyPhotoGroup(title = title, items = groupItems) }
        .sortedByDescending { it.title }
}

private fun Long.toTimestampMillis(): Long = if (this > 9_999_999_999L) this else this * 1000L
