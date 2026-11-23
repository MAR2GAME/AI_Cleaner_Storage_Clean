package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateFileSelectionRuleTest {
    @Test
    fun `scan evidence supports keeping any unselected verified copy`() {
        val group =
            DuplicateFileSelectionRule
                .buildGroups(listOf(file(1), file(2), file(3)), prefixHash = { "prefix" }) { "hash" }
                .single()
        val first = group.files.first()
        val selected =
            group.files
                .take(2)
                .map { it.uri }
                .toSet()
        assertEquals("hash", group.hash)
        assertTrue(
            DuplicateFileSelectionRule.canDelete(
                DuplicateFileSelectionModel(first, group),
                selected,
            ) { "hash" },
        )
    }

    @Test
    fun `selecting every copy blocks deletion`() {
        val group =
            DuplicateFileSelectionRule
                .buildGroups(listOf(file(1), file(2)), prefixHash = { "prefix" }) { "hash" }
                .single()
        val selected = group.files.map { it.uri }.toSet()
        group.files.forEach {
            assertFalse(
                DuplicateFileSelectionRule.canDelete(
                    DuplicateFileSelectionModel(it, group),
                    selected,
                ) { "hash" },
            )
        }
    }

    @Test
    fun `changed or unreadable retained copy blocks deletion`() {
        val group =
            DuplicateFileSelectionRule
                .buildGroups(listOf(file(1), file(2)), prefixHash = { "prefix" }) { "hash" }
                .single()
        val first = group.files.first()
        val selection = DuplicateFileSelectionModel(first, group)
        val selected = setOf(first.uri)
        assertFalse(DuplicateFileSelectionRule.canDelete(selection, selected) { if (it.id == 1L) "hash" else "changed" })
        assertFalse(DuplicateFileSelectionRule.canDelete(selection, selected) { if (it.id == 1L) "hash" else null })
    }

    @Test
    fun `missing scan evidence cannot authorize duplicate deletion`() {
        val file = file(1)
        val group = DuplicateFileGroupModel(hash = "", files = listOf(file))
        assertFalse(
            DuplicateFileSelectionRule.canDelete(
                DuplicateFileSelectionModel(file, group),
                setOf(file.uri),
            ) { "hash" },
        )
    }

    @Test
    fun pendingFileRequiresReadableMatchingUnselectedCopy() {
        val files = listOf(file(1), file(2))
        val group =
            DuplicateFileSelectionRule
                .buildGroups(files, prefixHash = { "prefix" }) { null }
                .single()
        val selection = DuplicateFileSelectionModel(files[1], group)
        val selected = setOf(files[1].uri)
        assertFalse(DuplicateFileSelectionRule.canDelete(selection, selected) { null })
        assertFalse(DuplicateFileSelectionRule.canDelete(selection, selected) { it.uri })
        assertTrue(DuplicateFileSelectionRule.canDelete(selection, selected) { "same" })
    }

    @Test
    fun `possible duplicate files only keep files that share a size`() {
        val single = file(1).copy(sizeBytes = 1L)
        val first = file(2)
        val second = file(3)

        assertEquals(
            listOf(first, second),
            DuplicateFileSelectionRule.possibleDuplicateFiles(listOf(single, first, second)),
        )
    }

    private fun file(id: Long) =
        ManagedFileModel(
            id = id,
            uri = ("content://media/external/file/$id"),
            path = null,
            name = "same.pdf",
            sizeBytes = 10L,
            modifiedSeconds = 0L,
            mimeType = "application/pdf",
            bucketName = null,
            kind = FileKind.Document,
        )
}
