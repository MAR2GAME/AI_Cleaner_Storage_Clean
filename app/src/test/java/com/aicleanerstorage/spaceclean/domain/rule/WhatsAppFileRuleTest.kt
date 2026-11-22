package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerGroup
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsAppFileRuleTest {
    @Test
    fun `recognizes WhatsApp paths and rejects other folders`() {
        assertTrue(WhatsAppFileRule.isCandidate(file("/storage/emulated/0/WhatsApp/Media/photo.jpg")))
        assertTrue(WhatsAppFileRule.isCandidate(file("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/photo.jpg")))
        assertFalse(WhatsAppFileRule.isCandidate(file("/storage/emulated/0/DCIM/Camera/photo.jpg")))
    }

    @Test
    fun `classifies images documents databases and cache`() {
        assertEquals(
            WhatsAppFileBucketModel(WhatsAppCleanerGroup.File, WhatsAppCleanerCategory.Images),
            WhatsAppFileRule.classify(file("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/photo.jpg")),
        )
        assertEquals(
            WhatsAppFileBucketModel(WhatsAppCleanerGroup.File, WhatsAppCleanerCategory.Documents),
            WhatsAppFileRule.classify(file("/storage/emulated/0/WhatsApp/Media/WhatsApp Documents/report.pdf")),
        )
        assertEquals(
            WhatsAppFileBucketModel(WhatsAppCleanerGroup.File, WhatsAppCleanerCategory.Databases),
            WhatsAppFileRule.classify(file("/storage/emulated/0/WhatsApp/Databases/msgstore.db")),
        )
        assertEquals(
            WhatsAppCleanerGroup.Cache,
            WhatsAppFileRule.classify(file("/storage/emulated/0/WhatsApp/Media/.Statuses/photo.jpg")).group,
        )
    }

    private fun file(path: String) = ManagedFileModel(
        id = 1L,
        uri = "content://media/external/file/1",
        path = path,
        name = path.substringAfterLast('/'),
        sizeBytes = 10L,
        modifiedSeconds = 1L,
        mimeType = null,
        bucketName = null,
        kind = when {
            path.endsWith(".jpg") -> FileKind.Image
            path.endsWith(".pdf") -> FileKind.Document
            else -> FileKind.Document
        },
    )
}
