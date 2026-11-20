package com.aicleanerstorage.spaceclean.app.view.screenshots.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

data class ScreenshotItem(
    val id: Int,
    val file: ManagedFileItemModel,
)

data class ScreenshotsManagerUiState(
    val showStopDialog: Boolean = false,
    val blockedPhase: FileOperationPhase? = null,
    val phase: FileOperationPhase = FileOperationPhase.Scanning,
    val items: List<ScreenshotItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val detailStartIndex: Int? = null,
    val deletedBytes: Long = 0L,
    val errorMessage: String? = null,
) {
    val displayItems: List<FileImageDisplayItem> get() = items.map { it.toDisplayItem() }
    val visibleIds: Set<Int> get() = items.map { it.id }.toSet()
    val allSelected: Boolean get() = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
    val selectedFiles: List<ManagedFileItemModel> get() = items.filter { it.id in selectedIds }.map { it.file }
    val selectedSizeBytes: Long get() = selectedFiles.sumOf { it.sizeBytes }
    val selectedUris: List<FileUri> get() = selectedFiles.map { it.uri }
    val resultSize: Pair<String, String> get() = FormatUtil.formatSize(deletedBytes).splitFileSizeLabel()
}

fun mapScreenshots(files: List<ManagedFileItemModel>, limit: Int = 60): List<ScreenshotItem> =
    files.take(limit).mapIndexed { index, file -> ScreenshotItem(id = index + 1, file = file) }

fun ScreenshotItem.toDisplayItem(): FileImageDisplayItem =
    managedFileToImageDisplayItem(id = id, file = file)
