package com.aicleanerstorage.spaceclean.data.local.storage

import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileRule
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

    @Test(expected = CancellationException::class)
    fun `hashing propagates cancellation`() {
        val file = directory.newFile("file").apply { writeText("data") }
        FileContentDigest.hashFile(file) { throw CancellationException() }
    }
}
