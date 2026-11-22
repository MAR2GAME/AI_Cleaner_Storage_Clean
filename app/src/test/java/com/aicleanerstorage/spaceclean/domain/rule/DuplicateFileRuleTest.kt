package com.aicleanerstorage.spaceclean.domain.rule

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateFileRuleTest {
    @Test
    fun `hashes only files that share a nonzero size`() {
        val hashed = mutableListOf<String>()
        val candidates =
            listOf(
                candidate("single", size = 1),
                candidate("first", size = 2),
                candidate("second", size = 2),
                candidate("empty-a", size = 0),
                candidate("empty-b", size = 0),
            )

        DuplicateFileRule.group(candidates) { value ->
            hashed += value
            "same"
        }

        assertEquals(listOf("first", "second"), hashed)
    }

    @Test
    fun `groups matching hashes and keeps newest file first`() {
        val candidates =
            listOf(
                candidate("old", size = 10, modified = 1),
                candidate("new", size = 10, modified = 2),
                candidate("different", size = 10, modified = 3),
            )

        val groups = DuplicateFileRule.group(candidates) { value -> if (value == "different") "other" else "match" }

        assertEquals(listOf(listOf("new", "old")), groups)
    }

    @Test
    fun `does not group same name and size when hashing fails`() {
        val candidates =
            listOf(
                candidate("a", name = "Report.pdf", size = 10),
                candidate("b", name = "report.PDF", size = 10),
            )

        val groups = DuplicateFileRule.group(candidates) { null }

        assertTrue(groups.isEmpty())
    }

    @Test
    fun `hash failure does not combine successfully hashed different contents`() {
        val candidates = listOf("a", "b", "unreadable").map { candidate(it, name = "same.pdf", size = 10) }
        val groups = DuplicateFileRule.group(candidates) { if (it == "unreadable") null else it }
        assertTrue(groups.isEmpty())
    }

    @Test
    fun `hash failure preserves only verified duplicate group`() {
        val candidates = listOf("a", "b", "unreadable").map { candidate(it, name = "same.pdf", size = 10) }
        val groups = DuplicateFileRule.group(candidates) { if (it == "unreadable") null else "same" }
        assertEquals(listOf(listOf("a", "b")), groups)
    }

    @Test
    fun `a file cannot be its own retained copy`() {
        assertFalse(DuplicateFileRule.hasVerifiedCopy("a", listOf("a"), "hash", { it }, { "hash" }))
        assertFalse(DuplicateFileRule.hasVerifiedCopy("a", emptyList(), "hash", { it }, { "hash" }))
        assertFalse(DuplicateFileRule.hasVerifiedCopy("a", listOf("b"), null, { it }, { "hash" }))
    }

    private fun candidate(
        value: String,
        name: String = value,
        size: Long,
        modified: Long = 0,
    ) = DuplicateFileRule.Candidate(
        value = value,
        identity = value,
        name = name,
        sizeBytes = size,
        modifiedMillis = modified,
    )
}
