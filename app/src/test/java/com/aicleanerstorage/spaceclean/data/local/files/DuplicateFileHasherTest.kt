package com.aicleanerstorage.spaceclean.data.local.files

import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Collections

class DuplicateFileHasherTest {
    @Test
    fun `prefetches prefixes only for files that share a size`() =
        runTest {
            val read = Collections.synchronizedList(mutableListOf<String>())
            val hasher =
                DuplicateFileHasher(
                    readPrefix = { item ->
                        read += item.name
                        FileContentDigest.PrefixDigest("prefix-${item.name}", isComplete = false)
                    },
                    readContent = { "content" },
                )

            hasher.prefetchPrefixes(listOf(file(1, size = 10), file(2, size = 10), file(3, size = 20)))

            assertEquals(2, read.size)
            assertEquals(setOf("file1", "file2"), read.toSet())
        }

    @Test
    fun `reuses the complete prefix hash instead of rereading the file`() =
        runTest {
            var contentReads = 0
            val hasher =
                DuplicateFileHasher(
                    readPrefix = { item -> FileContentDigest.PrefixDigest("prefix-${item.name}", isComplete = item.name == "a") },
                    readContent = { item ->
                        contentReads += 1
                        "content-${item.name}"
                    },
                )
            val complete = file(1, name = "a")
            val incomplete = file(2, name = "b")
            hasher.prefetchPrefixes(listOf(complete, incomplete))

            assertEquals("prefix-a", hasher.contentHash(complete))
            assertEquals(0, contentReads)
            assertEquals("content-b", hasher.contentHash(incomplete))
            assertEquals(1, contentReads)
            assertEquals("content-b", hasher.contentHash(incomplete))
            assertEquals(1, contentReads)
        }

    @Test
    fun `unreadable prefix is cached and the full hash decides`() =
        runTest {
            var prefixReads = 0
            var contentReads = 0
            val hasher =
                DuplicateFileHasher(
                    readPrefix = {
                        prefixReads += 1
                        null
                    },
                    readContent = {
                        contentReads += 1
                        "hash"
                    },
                )
            val files = listOf(file(1), file(2))
            hasher.prefetchPrefixes(files)

            assertNull(hasher.prefixHash(files[0]))
            assertNull(hasher.prefixHash(files[0]))
            assertEquals(2, prefixReads)
            assertEquals("hash", hasher.contentHash(files[0]))
            assertEquals(1, contentReads)
        }

    private fun file(
        id: Long,
        name: String = "file$id",
        size: Long = 10L,
    ) = ManagedFileModel(
        id = id,
        uri = "content://media/external/file/$id",
        path = null,
        name = name,
        sizeBytes = size,
        modifiedSeconds = 0L,
        mimeType = "application/pdf",
        bucketName = null,
        kind = FileKind.Document,
    )
}
