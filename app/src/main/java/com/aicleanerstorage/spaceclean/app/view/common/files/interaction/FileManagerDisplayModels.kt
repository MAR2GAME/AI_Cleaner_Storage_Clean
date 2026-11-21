package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class IndexedFileItem(
    val id: Int,
    val file: ManagedFileItem,
) {
    fun toImageDisplayItem(): FileImageDisplayItem = managedFileToImageDisplayItem(id, file)
}

fun List<ManagedFileItem>.withSelectionIds(): List<IndexedFileItem> = mapIndexed { index, file -> IndexedFileItem(index + 1, file) }

data class FileManagerTabDisplayItem(
    val title: String,
    val sizeLabel: String = "",
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
    val items: List<FileImageDisplayItem>,
)

data class FileImageDateGroupDisplayItem(
    val title: String,
    val items: List<FileImageDisplayItem>,
)

enum class FileListDisplayStyle {
    Default,
    Documents,
}

enum class FileListIconKind {
    LargeVideo,
    Document,
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
        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
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
        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
        iconKind = iconKind,
        bucketName = file.bucketName,
        path = file.path,
    )

fun buildMediaTabs(
    titles: List<String>,
    items: List<IndexedFileItem>,
): List<FileManagerTabDisplayItem> {
    val displayItems = items.map { it.toImageDisplayItem() }
    val filesById = items.associate { it.id to it.file }
    return titles.map { title ->
        val files = filterMediaGridItems(title, displayItems).mapNotNull { filesById[it.id] }
        FileManagerTabDisplayItem(title, totalSizeLabel(files))
    }
}

fun buildListTabs(
    items: List<IndexedFileItem>,
    iconKind: FileListIconKind,
): List<FileManagerTabDisplayItem> {
    val displayItems = items.map { managedFileToListDisplayItem(it.id, it.file, iconKind) }
    val filesById = items.associate { it.id to it.file }
    return listOf("All", "Download", "Other").map { title ->
        val files = filterFileManagerListItems(title, displayItems).mapNotNull { filesById[it.id] }
        FileManagerTabDisplayItem(title, totalSizeLabel(files))
    }
}

fun formatManagedFileDate(modifiedSeconds: Long): String {
    val millis = if (modifiedSeconds > 9_999_999_999L) modifiedSeconds else modifiedSeconds * 1000L
    return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US).format(Date(millis))
}

fun managedFileMeta(file: ManagedFileItem): String =
    "${formatManagedFileDate(file.modifiedSeconds)} ${FormatUtil.formatSize(file.sizeBytes)}"

fun totalSizeLabel(files: List<ManagedFileItem>): String = FormatUtil.formatSize(files.sumOf { it.sizeBytes })

fun String.splitFileSizeLabel(): Pair<String, String> = substringBefore(" ") to substringAfter(" ", "B")
