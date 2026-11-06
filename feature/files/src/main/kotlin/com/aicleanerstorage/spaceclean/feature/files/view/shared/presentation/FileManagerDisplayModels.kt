package com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation

import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileImageDateGroupDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListIconKind
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileListDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileDetailDisplayPreview
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileDetailDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.managedFileToListDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.buildMediaTabs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.buildListTabs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.formatManagedFileDate
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.managedFileMeta
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.totalSizeLabel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.filterMediaGridItems
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.filterFileManagerListItems


import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileManagerTabDisplayItem(
    val title: String,
    val sizeLabel: String = ""
)

data class FileImageDisplayItem(
    val id: Int,
    val name: String,
    val meta: String,
    val sizeLabel: String,
    val paletteIndex: Int,
    val uri: FileUri?,
    val bucketName: String?,
    val path: String?,
    val bestPhoto: Boolean = false,
)

data class FileImageGroupDisplayItem(
    val count: Int,
    val items: List<FileImageDisplayItem>
)

data class FileImageDateGroupDisplayItem(
    val title: String,
    val items: List<FileImageDisplayItem>,
)

enum class FileListDisplayStyle {
    Default,
    Documents
}

enum class FileListIconKind {
    LargeVideo,
    Document
}

data class FileListDisplayItem(
    val id: Int,
    val name: String,
    val meta: String,
    val sizeLabel: String,
    val iconKind: FileListIconKind,
    val bucketName: String?,
    val path: String?,
)

sealed interface FileDetailDisplayPreview {
    data class MediaPreview(
        val item: FileImageDisplayItem,
    ) : FileDetailDisplayPreview

    data class FileIconPreview(
        val kind: FileListIconKind,
    ) : FileDetailDisplayPreview
}

data class FileDetailDisplayItem(
    val id: Int,
    val name: String,
    val meta: String,
    val sizeLabel: String,
    val preview: FileDetailDisplayPreview,
)

fun FileImageDisplayItem.toFileDetailDisplayItem(): FileDetailDisplayItem =
    FileDetailDisplayItem(
        id = id,
        name = name,
        meta = meta,
        sizeLabel = sizeLabel,
        preview = FileDetailDisplayPreview.MediaPreview(this),
    )

fun FileListDisplayItem.toFileDetailDisplayItem(): FileDetailDisplayItem =
    FileDetailDisplayItem(
        id = id,
        name = name,
        meta = meta,
        sizeLabel = sizeLabel,
        preview = FileDetailDisplayPreview.FileIconPreview(iconKind),
    )

fun managedFileToImageDisplayItem(
    id: Int,
    file: ManagedFileItem,
    bestPhoto: Boolean = false,
): FileImageDisplayItem =
    FileImageDisplayItem(
        id = id,
        name = file.name,
        meta = managedFileMeta(file),
        sizeLabel = file.formattedSize,
        paletteIndex = id - 1,
        uri = file.uri,
        bucketName = file.bucketName,
        path = file.path,
        bestPhoto = bestPhoto,
    )

fun managedFileToListDisplayItem(
    id: Int,
    file: ManagedFileItem,
    iconKind: FileListIconKind,
): FileListDisplayItem =
    FileListDisplayItem(
        id = id,
        name = file.name,
        meta = managedFileMeta(file),
        sizeLabel = file.formattedSize,
        iconKind = iconKind,
        bucketName = file.bucketName,
        path = file.path,
    )

fun buildMediaTabs(
    titles: List<String>,
    items: List<FileImageDisplayItem>,
    fileForId: (Int) -> ManagedFileItem?,
): List<FileManagerTabDisplayItem> =
    titles.map { title ->
        val files = filterMediaGridItems(title, items).mapNotNull { fileForId(it.id) }
        FileManagerTabDisplayItem(title = title, sizeLabel = totalSizeLabel(files))
    }

fun buildListTabs(
    titles: List<String>,
    items: List<FileListDisplayItem>,
    fileForId: (Int) -> ManagedFileItem?,
): List<FileManagerTabDisplayItem> =
    titles.map { title ->
        val files = filterFileManagerListItems(title, items).mapNotNull { fileForId(it.id) }
        FileManagerTabDisplayItem(title = title, sizeLabel = totalSizeLabel(files))
    }

fun formatManagedFileDate(modifiedSeconds: Long): String {
    val millis = if (modifiedSeconds > 9_999_999_999L) modifiedSeconds else modifiedSeconds * 1000L
    return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US).format(Date(millis))
}

fun managedFileMeta(file: ManagedFileItem): String =
    "${formatManagedFileDate(file.modifiedSeconds)} ${file.formattedSize}"

fun totalSizeLabel(files: List<ManagedFileItem>): String =
    FileSizeFormatter.format(files.sumOf { it.sizeBytes })

fun String.splitFileSizeLabel(): Pair<String, String> =
    substringBefore(" ") to substringAfter(" ", "B")
