package com.aicleanerstorage.spaceclean.data.local.junkclean

import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.DuplicateFileScanner
import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDuplicateMetadataModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DuplicateFileSafetyTest {
    @get:Rule val directory = TemporaryFolder()

    @Test
    fun `junk grouping separates pending files from verified matches`() {
        val files =
            listOf("a", "b", "c").mapIndexed { index, path ->
                DuplicateFileScanner.ScannedFile(path, "same.pdf", 10L, index.toLong())
            }
        val scanner = DuplicateFileScanner()
        assertEquals(files.reversed(), scanner.buildDuplicateFileGroups(files) { null }.single().files)
        val groups = scanner.buildDuplicateFileGroups(files) { if (it.path == "c") null else "hash" }
        assertEquals(listOf(files[1], files[0]), groups.single { it.hash != null }.files)
        assertEquals(listOf(files[1], files[2]), groups.single { it.hash == null }.files)
    }

    @Test
    fun `junk deletion requires unchanged retained file outside deletion batch`() {
        val original = directory.newFile("original").apply { writeText("same") }
        val copy = directory.newFile("copy").apply { writeText("same") }
        val item =
            JunkFileModel(
                "copy",
                copy.path,
                copy.name,
                copy.length(),
                JunkCategory.DUPLICATE,
                modifiedMillis = copy.lastModified(),
                duplicate = JunkDuplicateMetadataModel(hash = FileContentDigest.hashFile(copy), originalPath = original.path),
            )
        assertTrue(JunkFileDeleter.canDeleteDuplicate(item, setOf(copy.path)))
        assertFalse(JunkFileDeleter.canDeleteDuplicate(item, setOf(copy.path, original.path)))
        assertFalse(JunkFileDeleter.canDeleteDuplicate(item, setOf(directory.root.path)))
        assertFalse(JunkFileDeleter.canDeleteDuplicate(item.copy(duplicate = item.duplicate?.copy(hash = null)), setOf(copy.path)))
        original.writeText("else")
        assertFalse(JunkFileDeleter.canDeleteDuplicate(item, setOf(copy.path)))
        original.delete()
        assertFalse(JunkFileDeleter.canDeleteDuplicate(item, setOf(copy.path)))
    }
}
