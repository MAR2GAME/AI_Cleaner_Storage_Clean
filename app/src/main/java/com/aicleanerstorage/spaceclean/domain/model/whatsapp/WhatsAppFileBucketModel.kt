package com.aicleanerstorage.spaceclean.domain.model.whatsapp

/** Identifies one category section inside a WhatsApp cleaner group. */
data class WhatsAppFileBucketModel(
    val group: WhatsAppCleanerGroup,
    val category: WhatsAppCleanerCategory,
)
