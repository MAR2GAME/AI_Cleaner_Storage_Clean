package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterFileManagerListItems
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.filterMediaGridItems

fun filterMediaGridItems(
    tabTitle: String,
    items: List<FileImageDisplayItem>,
): List<FileImageDisplayItem> {
    fun FileImageDisplayItem.matchesFolder(name: String): Boolean =
        bucketName?.contains(name, ignoreCase = true) == true ||
            path?.contains("/$name/", ignoreCase = true) == true

    return when (tabTitle) {
        "DCIM" -> items.filter { it.matchesFolder("DCIM") || it.matchesFolder("Camera") }
        "Download" -> items.filter { it.matchesFolder("Download") }
        "Music" -> items.filter { it.matchesFolder("Music") || it.matchesFolder("Audio") }.ifEmpty { items }
        "Other" -> items.filterNot { it.matchesFolder("DCIM") || it.matchesFolder("Camera") || it.matchesFolder("Download") }
        else -> items
    }
}

fun filterFileManagerListItems(
    tabTitle: String,
    items: List<FileListDisplayItem>,
): List<FileListDisplayItem> {
    fun FileListDisplayItem.matchesFolder(name: String): Boolean =
        bucketName?.contains(name, ignoreCase = true) == true ||
            path?.contains("/$name/", ignoreCase = true) == true

    return when (tabTitle) {
        "Download" -> items.filter { it.matchesFolder("Download") }
        "Other" -> items.filterNot { it.matchesFolder("Download") }
        else -> items
    }
}
