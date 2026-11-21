package com.aicleanerstorage.cleanerkit.clean.internal

import com.aicleanerstorage.cleanerkit.clean.internal.scanner.DuplicateFileScanner
import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.internal.storage.FileContentDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DuplicateFileSafetyTest {
    @get:Rule val directory = TemporaryFolder()

    @Test
    fun `junk grouping excludes failed hashes without discarding verified matches`() {
        val files = listOf("a", "b", "c").mapIndexed { index, path ->
            DuplicateFileScanner.ScannedFile(path, "same.pdf", 10L, index.toLong())
        }
        val scanner = DuplicateFileScanner()
        assertTrue(scanner.buildDuplicateFileGroups(files) { null }.isEmpty())
        assertTrue(scanner.buildDuplicateFileGroups(files) { if (it.path == "c") null else it.path }.isEmpty())
        assertEquals(listOf(listOf(files[1], files[0])), scanner.buildDuplicateFileGroups(files) {
            if (it.path == "c") null else "hash"
        })
    }

    @Test
    fun `junk deletion requires unchanged retained file outside deletion batch`() {
        val original = directory.newFile("original").apply { writeText("same") }
        val copy = directory.newFile("copy").apply { writeText("same") }
        val item = JunkFile(
            "copy", copy.path, copy.name, copy.length(), JunkCategory.DUPLICATE,
            duplicateHash = FileContentDigest.hashFile(copy), duplicateOriginalPath = original.path,
        )
        assertTrue(JunkFileDeleteHelper.canDeleteDuplicate(item, setOf(copy.path)))
        assertFalse(JunkFileDeleteHelper.canDeleteDuplicate(item, setOf(copy.path, original.path)))
        assertFalse(JunkFileDeleteHelper.canDeleteDuplicate(item, setOf(directory.root.path)))
        assertFalse(JunkFileDeleteHelper.canDeleteDuplicate(item.copy(duplicateHash = null), setOf(copy.path)))
        original.writeText("else")
        assertFalse(JunkFileDeleteHelper.canDeleteDuplicate(item, setOf(copy.path)))
        original.delete()
        assertFalse(JunkFileDeleteHelper.canDeleteDuplicate(item, setOf(copy.path)))
    }
}
