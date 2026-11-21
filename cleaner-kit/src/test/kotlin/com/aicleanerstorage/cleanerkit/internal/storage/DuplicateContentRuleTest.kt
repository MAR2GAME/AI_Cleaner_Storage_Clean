package com.aicleanerstorage.cleanerkit.internal.storage

import java.io.RandomAccessFile
import java.util.concurrent.CancellationException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DuplicateContentRuleTest {
    @get:Rule val directory = TemporaryFolder()

    @Test
    fun `changed or missing retained file blocks deletion`() {
        val candidate = directory.newFile("candidate").apply { writeText("same") }
        val retained = directory.newFile("retained").apply { writeText("same") }
        val expected = FileContentDigest.hashFile(candidate)
        fun allowed() = DuplicateContentRule.hasVerifiedCopy(
            candidate, listOf(retained), expected,
            identity = { it.canonicalPath }, contentHash = { FileContentDigest.hashFile(it) },
        )
        assertTrue(allowed())
        retained.writeText("else")
        assertFalse(allowed())
        retained.delete()
        assertFalse(allowed())
    }

    @Test
    fun `changed candidate is rejected even if both files now match each other`() {
        val candidate = directory.newFile("candidate").apply { writeText("before") }
        val retained = directory.newFile("retained").apply { writeText("before") }
        val expected = FileContentDigest.hashFile(candidate)
        candidate.writeText("after!")
        retained.writeText("after!")
        assertFalse(DuplicateContentRule.hasVerifiedCopy(
            candidate, listOf(retained), expected,
            identity = { it.canonicalPath }, contentHash = { FileContentDigest.hashFile(it) },
        ))
    }

    @Test
    fun `a file cannot be its own retained copy`() {
        assertFalse(DuplicateContentRule.hasVerifiedCopy("a", listOf("a"), "hash", { it }, { "hash" }))
        assertFalse(DuplicateContentRule.hasVerifiedCopy("a", emptyList(), "hash", { it }, { "hash" }))
        assertFalse(DuplicateContentRule.hasVerifiedCopy("a", listOf("b"), null, { it }, { "hash" }))
    }

    @Test
    fun `files above junk scanner limit are not hashed`() {
        val file = directory.newFile("large")
        RandomAccessFile(file, "rw").use { it.setLength(100L * 1024 * 1024 + 1) }
        assertNull(FileContentDigest.hashFile(file, maxBytes = 100L * 1024 * 1024))
    }

    @Test(expected = CancellationException::class)
    fun `hashing propagates cancellation`() {
        val file = directory.newFile("file").apply { writeText("data") }
        FileContentDigest.hashFile(file) { throw CancellationException() }
    }
}

