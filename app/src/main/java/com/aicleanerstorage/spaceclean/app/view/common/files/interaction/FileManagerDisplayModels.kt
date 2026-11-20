package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDateGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListIconKind
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailDisplayPreview
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileToListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.buildMediaTabs
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.buildListTabs
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.formatManagedFileDate
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.managedFileMeta
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.totalSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterMediaGridItems
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterFileManagerListItems


import com.aicleanerstorage.spaceclean.base.util.FormatUtil
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
    file: ManagedFileItemModel,
    bestPhoto: Boolean = false,
): FileImageDisplayItem =
    FileImageDisplayItem(
        id = id,
        name = file.name,
        meta = managedFileMeta(file),
        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
        paletteIndex = id - 1,
        uri = file.uri,
        bucketName = file.bucketName,
        path = file.path,
        bestPhoto = bestPhoto,
    )

fun managedFileToListDisplayItem(
    id: Int,
    file: ManagedFileItemModel,
    iconKind: FileListIconKind,
): FileListDisplayItem =
    FileListDisplayItem(
        id = id,
        name = file.name,
        meta = managedFileMeta(file),
        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
        iconKind = iconKind,
        bucketName = file.bucketName,
        path = file.path,
    )

fun buildMediaTabs(
    titles: List<String>,
    items: List<FileImageDisplayItem>,
    fileForId: (Int) -> ManagedFileItemModel?,
): List<FileManagerTabDisplayItem> =
    titles.map { title ->
        val files = filterMediaGridItems(title, items).mapNotNull { fileForId(it.id) }
        FileManagerTabDisplayItem(title = title, sizeLabel = totalSizeLabel(files))
    }

fun buildListTabs(
    titles: List<String>,
    items: List<FileListDisplayItem>,
    fileForId: (Int) -> ManagedFileItemModel?,
): List<FileManagerTabDisplayItem> =
    titles.map { title ->
        val files = filterFileManagerListItems(title, items).mapNotNull { fileForId(it.id) }
        FileManagerTabDisplayItem(title = title, sizeLabel = totalSizeLabel(files))
    }

fun formatManagedFileDate(modifiedSeconds: Long): String {
    val millis = if (modifiedSeconds > 9_999_999_999L) modifiedSeconds else modifiedSeconds * 1000L
    return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US).format(Date(millis))
}

fun managedFileMeta(file: ManagedFileItemModel): String =
    "${formatManagedFileDate(file.modifiedSeconds)} ${FormatUtil.formatSize(file.sizeBytes)}"

fun totalSizeLabel(files: List<ManagedFileItemModel>): String =
    FormatUtil.formatSize(files.sumOf { it.sizeBytes })

fun String.splitFileSizeLabel(): Pair<String, String> =
    substringBefore(" ") to substringAfter(" ", "B")
