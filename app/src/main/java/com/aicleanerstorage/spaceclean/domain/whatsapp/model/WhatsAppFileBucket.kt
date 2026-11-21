package com.aicleanerstorage.spaceclean.domain.whatsapp.model

/** Identifies one category section inside a WhatsApp cleaner group. */
data class WhatsAppFileBucket(
    val group: WhatsAppCleanerGroup,
    val category: WhatsAppCleanerCategory,
)
