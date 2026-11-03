package com.aicleanerstorage.spaceclean.common.files

import java.io.RandomAccessFile
import java.util.concurrent.CancellationException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DuplicateContentGuardTest {
    @get:Rule val directory = TemporaryFolder()

    @Test
    fun `changed or missing retained file blocks deletion`() {
        val candidate = directory.newFile("candidate").apply { writeText("same") }
        val retained = directory.newFile("retained").apply { writeText("same") }
        val expected = DuplicateContentGuard.hashFile(candidate)
        fun allowed() = DuplicateContentGuard.hasVerifiedCopy(
            candidate, listOf(retained), expected,
            identity = { it.canonicalPath }, contentHash = { DuplicateContentGuard.hashFile(it) },
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
        val expected = DuplicateContentGuard.hashFile(candidate)
        candidate.writeText("after!")
        retained.writeText("after!")
        assertFalse(DuplicateContentGuard.hasVerifiedCopy(
            candidate, listOf(retained), expected,
            identity = { it.canonicalPath }, contentHash = { DuplicateContentGuard.hashFile(it) },
        ))
    }

    @Test
    fun `a file cannot be its own retained copy`() {
        assertFalse(DuplicateContentGuard.hasVerifiedCopy("a", listOf("a"), "hash", { it }, { "hash" }))
        assertFalse(DuplicateContentGuard.hasVerifiedCopy("a", emptyList(), "hash", { it }, { "hash" }))
        assertFalse(DuplicateContentGuard.hasVerifiedCopy("a", listOf("b"), null, { it }, { "hash" }))
    }

    @Test
    fun `files above junk scanner limit are not hashed`() {
        val file = directory.newFile("large")
        RandomAccessFile(file, "rw").use { it.setLength(100L * 1024 * 1024 + 1) }
        assertNull(DuplicateContentGuard.hashFile(file, maxBytes = 100L * 1024 * 1024))
    }

    @Test(expected = CancellationException::class)
    fun `hashing propagates cancellation`() {
        val file = directory.newFile("file").apply { writeText("data") }
        DuplicateContentGuard.hashFile(file) { throw CancellationException() }
    }
}
