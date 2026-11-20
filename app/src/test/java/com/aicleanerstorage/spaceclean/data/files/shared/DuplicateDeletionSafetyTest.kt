package com.aicleanerstorage.spaceclean.data.repository.files

import com.aicleanerstorage.spaceclean.data.local.DuplicateFileSelection

import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateDeletionSafetyTest {
    @Test
    fun `scan evidence supports keeping any unselected verified copy`() {
        val group = DuplicateFileSelection.buildDuplicateFileGroups(listOf(file(1), file(2), file(3))) { "hash" }.single()
        val selected = group.take(2).map { it.uri.value }.toSet()
        assertEquals("hash", group.first().duplicateHash)
        assertTrue(DuplicateFileSelection.canDeleteDuplicate(group.first(), selected) { "hash" })
    }

    @Test
    fun `selecting every copy blocks deletion`() {
        val group = DuplicateFileSelection.buildDuplicateFileGroups(listOf(file(1), file(2))) { "hash" }.single()
        val selected = group.map { it.uri.value }.toSet()
        group.forEach { assertFalse(DuplicateFileSelection.canDeleteDuplicate(it, selected) { "hash" }) }
    }

    @Test
    fun `changed or unreadable retained copy blocks deletion`() {
        val group = DuplicateFileSelection.buildDuplicateFileGroups(listOf(file(1), file(2))) { "hash" }.single()
        val selected = setOf(group.first().uri.value)
        assertFalse(DuplicateFileSelection.canDeleteDuplicate(group.first(), selected) { if (it.id == 1L) "hash" else "changed" })
        assertFalse(DuplicateFileSelection.canDeleteDuplicate(group.first(), selected) { if (it.id == 1L) "hash" else null })
    }

    @Test
    fun `missing scan evidence cannot authorize duplicate deletion`() {
        assertFalse(DuplicateFileSelection.canDeleteDuplicate(file(1), setOf(file(1).uri.value)) { "hash" })
    }

    private fun file(id: Long) = ManagedFileItemModel(
        id = id,
        uri = FileUri("content://media/external/file/$id"),
        path = null,
        name = "same.pdf",
        sizeBytes = 10L,
        modifiedSeconds = 0L,
        mimeType = "application/pdf",
        bucketName = null,
        type = ManagedFileType.Document,
    )
}
