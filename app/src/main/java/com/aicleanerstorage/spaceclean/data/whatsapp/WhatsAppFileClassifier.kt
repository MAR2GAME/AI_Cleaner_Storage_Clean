package com.aicleanerstorage.spaceclean.data.whatsapp

import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType

internal fun classifyWhatsAppFile(file: ManagedFileItem): WhatsAppFileBucket {
    val source = listOfNotNull(file.path, file.bucketName, file.name).joinToString("/").lowercase()
    val extension = file.name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    val group = if (CACHE_MARKERS.any(source::contains)) WhatsAppCleanerGroup.Cache else WhatsAppCleanerGroup.File
    val category = when {
        "database" in source || extension in DATABASE_EXTENSIONS -> WhatsAppCleanerCategory.Databases
        "document" in source || (file.type == ManagedFileType.Document && extension in DOCUMENT_EXTENSIONS) -> WhatsAppCleanerCategory.Documents
        file.type == ManagedFileType.Image -> WhatsAppCleanerCategory.Images
        file.type == ManagedFileType.Video -> WhatsAppCleanerCategory.Videos
        file.type == ManagedFileType.Audio -> WhatsAppCleanerCategory.Audios
        else -> WhatsAppCleanerCategory.Other
    }
    return WhatsAppFileBucket(group, category)
}

private val CACHE_MARKERS = listOf("cache", "tmp", "temp", ".trash", ".statuses", "thumb")
private val DATABASE_EXTENSIONS = setOf("db", "crypt", "crypt5", "crypt7", "crypt8", "crypt12", "crypt14", "crypt15")
private val DOCUMENT_EXTENSIONS = setOf(
    "pdf",
    "doc",
    "docx",
    "xls",
    "xlsx",
    "ppt",
    "pptx",
    "txt",
    "csv",
    "rtf",
    "zip",
    "rar",
    "7z",
    "apk",
    "json",
    "xml",
)
