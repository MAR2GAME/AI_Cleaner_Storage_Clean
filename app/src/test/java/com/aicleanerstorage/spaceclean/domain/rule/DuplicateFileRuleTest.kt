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

        DuplicateFileRule.group(candidates, prefixHash = { "prefix" }) { value ->
            hashed += value
            "same"
        }

        assertEquals(listOf("first", "second"), hashed)
    }

    @Test
    fun `skips full hashing when prefixes differ`() {
        val hashed = mutableListOf<String>()
        val candidates = listOf(candidate("first", size = 10), candidate("second", size = 10))

        val groups =
            DuplicateFileRule.group(candidates, prefixHash = { it }) { value ->
                hashed += value
                "same"
            }

        assertTrue(groups.isEmpty())
        assertTrue(hashed.isEmpty())
    }

    @Test
    fun `full hashing decides files whose prefixes match`() {
        val candidates = listOf(candidate("first", size = 10), candidate("second", size = 10))

        val groups =
            DuplicateFileRule.group(candidates, prefixHash = { "prefix" }) { value ->
                if (value == "first") "a" else "b"
            }

        assertTrue(groups.isEmpty())
    }

    @Test
    fun `unknown prefix falls back to full hashing`() {
        val hashed = mutableListOf<String>()
        val candidates =
            listOf(
                candidate("first", size = 10),
                candidate("second", size = 10),
                candidate("third", size = 10),
            )

        DuplicateFileRule.group(candidates, prefixHash = { if (it == "third") null else it }) { value ->
            hashed += value
            "same"
        }

        assertEquals(listOf("first", "second", "third"), hashed)
    }

    @Test
    fun `groups matching hashes and keeps newest file first`() {
        val candidates =
            listOf(
                candidate("old", size = 10, modified = 1),
                candidate("new", size = 10, modified = 2),
                candidate("different", size = 10, modified = 3),
            )

        val groups =
            DuplicateFileRule.group(candidates, prefixHash = { "prefix" }) { value ->
                if (value == "different") "other" else "match"
            }

        assertEquals(listOf(DuplicateFileRule.Group("match", listOf("new", "old"))), groups)
    }

    @Test
    fun `marks same name and size as pending when hashing fails`() {
        val candidates =
            listOf(
                candidate("a", name = "Report.pdf", size = 10),
                candidate("b", name = "report.PDF", size = 10),
            )

        val groups = DuplicateFileRule.group(candidates, prefixHash = { null }) { null }

        assertEquals(DuplicateFileRule.Group(null, listOf("a", "b")), groups.single())
    }

    @Test
    fun `hash failure does not combine successfully hashed different contents`() {
        val candidates = listOf("a", "b", "unreadable").map { candidate(it, name = "same.pdf", size = 10) }
        val groups =
            DuplicateFileRule.group(candidates, prefixHash = { if (it == "unreadable") null else "prefix" }) { value ->
                if (value == "unreadable") null else value
            }
        assertTrue(groups.single().hash == null)
        assertEquals(listOf("a", "unreadable"), groups.single().files)
    }

    @Test
    fun `verified group stays separate from pending candidates`() {
        val candidates = listOf("a", "b", "unreadable").map { candidate(it, name = "same.pdf", size = 10) }
        val groups =
            DuplicateFileRule.group(candidates, prefixHash = { if (it == "unreadable") null else "prefix" }) { value ->
                if (value == "unreadable") null else "same"
            }
        assertEquals(listOf("a", "b"), groups.single { it.hash != null }.files)
        assertEquals(listOf("a", "unreadable"), groups.single { it.hash == null }.files)
    }

    @Test
    fun `a file cannot be its own retained copy`() {
        assertFalse(DuplicateFileRule.hasVerifiedCopy("a", listOf("a"), "hash", { it }, { "hash" }))
        assertFalse(DuplicateFileRule.hasVerifiedCopy("a", emptyList(), "hash", { it }, { "hash" }))
        assertTrue(DuplicateFileRule.hasVerifiedCopy("a", listOf("b"), null, { it }, { "hash" }))
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
