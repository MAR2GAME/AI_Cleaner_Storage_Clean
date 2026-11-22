package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileClassificationRuleTest {
    @Test
    fun `classifies media and documents by extension and mime`() {
        assertTrue(FileClassificationRule.isMediaFile("photo.JPG", null))
        assertTrue(FileClassificationRule.isMediaFile("clip", "video/mp4"))
        assertTrue(FileClassificationRule.isDocumentFile("report.pdf", null))
        assertTrue(FileClassificationRule.isDocumentFile("notes", "text/plain"))
        assertFalse(FileClassificationRule.isDocumentFile("photo.jpg", "image/jpeg"))
        assertTrue(FileClassificationRule.isDocumentFile("archive.zip", "application/zip"))
    }

    @Test
    fun `infers file kind from extension`() {
        assertEquals(FileKind.Image, FileClassificationRule.typeForName("a.heic"))
        assertEquals(FileKind.Video, FileClassificationRule.typeForName("a.mkv"))
        assertEquals(FileKind.Audio, FileClassificationRule.typeForName("a.flac"))
        assertEquals(FileKind.Document, FileClassificationRule.typeForName("a.pdf"))
    }

    @Test
    fun `skips hidden files and system directories`() {
        assertTrue(FileClassificationRule.shouldSkipPath("/storage/emulated/0/.nomedia", ".nomedia"))
        assertTrue(FileClassificationRule.shouldSkipPath("/system/etc/file", "file"))
        assertFalse(FileClassificationRule.shouldSkipPath("/storage/emulated/0/DCIM/photo.jpg", "photo.jpg"))
    }

    @Test
    fun `identity prefers path and falls back to uri`() {
        val path = File("a/../b").path
        assertEquals(File(path).canonicalPath, FileClassificationRule.identity(path, "content://1"))
        assertEquals("content://1", FileClassificationRule.identity(null, "content://1"))
        assertEquals("content://1", FileClassificationRule.identity("", "content://1"))
    }
}
