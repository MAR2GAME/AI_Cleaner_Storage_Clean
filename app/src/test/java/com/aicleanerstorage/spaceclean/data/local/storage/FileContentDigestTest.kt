package com.aicleanerstorage.spaceclean.data.local.storage

import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.RandomAccessFile
import java.util.concurrent.CancellationException

class FileContentDigestTest {
    @get:Rule val directory = TemporaryFolder()

    @Test
    fun `changed or missing retained file blocks deletion`() {
        val candidate = directory.newFile("candidate").apply { writeText("same") }
        val retained = directory.newFile("retained").apply { writeText("same") }
        val expected = FileContentDigest.hashFile(candidate)

        fun allowed() =
            DuplicateFileRule.hasVerifiedCopy(
                candidate,
                listOf(retained),
                expected,
                identity = { it.canonicalPath },
                contentHash = { FileContentDigest.hashFile(it) },
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
        assertFalse(
            DuplicateFileRule.hasVerifiedCopy(
                candidate,
                listOf(retained),
                expected,
                identity = { it.canonicalPath },
                contentHash = { FileContentDigest.hashFile(it) },
            ),
        )
    }

    @Test
    fun `files above junk scanner limit are not hashed`() {
        val file = directory.newFile("large")
        RandomAccessFile(file, "rw").use { it.setLength(100L * 1024 * 1024 + 1) }
        assertNull(FileContentDigest.hashFile(file, maxBytes = 100L * 1024 * 1024))
    }

    @Test
    fun `prefix digest of a short file is the full content hash`() {
        val file = directory.newFile("short").apply { writeText("same") }

        val digest = FileContentDigest.hashFilePrefix(file)

        assertEquals(FileContentDigest.hashFile(file), digest?.hash)
        assertTrue(digest?.isComplete == true)
    }

    @Test
    fun `prefix digest stops at the window and reports an incomplete read`() {
        val file = directory.newFile("long")
        val content = ByteArray(FileContentDigest.PREFIX_HASH_BYTES + 10) { it.toByte() }
        file.writeBytes(content)

        val digest = FileContentDigest.hashFilePrefix(file)

        assertFalse(digest?.isComplete == true)
        assertEquals(
            FileContentDigest.sha256(content.inputStream(0, FileContentDigest.PREFIX_HASH_BYTES)),
            digest?.hash,
        )
    }

    @Test(expected = CancellationException::class)
    fun `prefix hashing propagates cancellation`() {
        val file = directory.newFile("file").apply { writeText("data") }
        FileContentDigest.hashFilePrefix(file) { throw CancellationException() }
    }

    @Test(expected = CancellationException::class)
    fun `hashing propagates cancellation`() {
        val file = directory.newFile("file").apply { writeText("data") }
        FileContentDigest.hashFile(file) { throw CancellationException() }
    }
}
