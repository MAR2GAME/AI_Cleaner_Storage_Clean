package com.aicleanerstorage.spaceclean.data.whatsapp

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import java.util.Locale

internal suspend fun FileRepository.loadWhatsAppFileGroups(): Map<WhatsAppFileBucket, List<ManagedFileItem>> =
    loadFilesInDirectories(WHATSAPP_SCAN_DIRECTORIES, ::isWhatsAppCandidate)
        .filter { it.sizeBytes > 0L }
        .groupBy(::classifyWhatsAppFile)

private fun isWhatsAppCandidate(item: ManagedFileItem): Boolean {
    val source = listOfNotNull(item.path, item.bucketName, item.name).joinToString("/").lowercase(Locale.US)
    return WHATSAPP_PATH_MARKERS.any { it in source }
}

private val WHATSAPP_SCAN_DIRECTORIES =
    listOf(
        "WhatsApp", "WhatsApp Business", "Android/media/com.whatsapp/WhatsApp",
        "Android/media/com.whatsapp.w4b/WhatsApp Business", "Android/media/com.whatsapp.w4b/WhatsApp",
        "Android/media/com.gbwhatsapp/GBWhatsApp", "Android/media/com.yowhatsapp/YoWhatsApp",
        "Android/media/com.fmwhatsapp/FMWhatsApp",
    )

private val WHATSAPP_PATH_MARKERS =
    listOf(
        "/whatsapp/", "/whatsapp business/", "/gbwhatsapp/", "/yowhatsapp/", "/fmwhatsapp/",
        "com.whatsapp", "com.whatsapp.w4b",
    )
