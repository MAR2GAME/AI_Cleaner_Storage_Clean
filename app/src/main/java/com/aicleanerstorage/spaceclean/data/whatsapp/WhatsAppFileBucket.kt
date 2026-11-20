package com.aicleanerstorage.spaceclean.data.whatsapp

enum class WhatsAppCleanerGroup {
    Cache,
    File,
}

enum class WhatsAppCleanerCategory {
    Images,
    Videos,
    Audios,
    Documents,
    Databases,
    Other,
}

data class WhatsAppFileBucket(
    val group: WhatsAppCleanerGroup,
    val category: WhatsAppCleanerCategory,
)
