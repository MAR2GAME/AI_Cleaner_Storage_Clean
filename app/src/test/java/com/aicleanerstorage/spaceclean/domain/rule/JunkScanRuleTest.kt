package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDuplicateMetadataModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import org.junit.Assert.assertEquals
import org.junit.Test

class JunkScanRuleTest {
    @Test
    fun `normalize keeps duplicate category first and one entry per path`() {
        val cache = file("cache", "/tmp/a", JunkCategory.CACHE)
        val duplicate = file("duplicate", "/tmp/a", JunkCategory.DUPLICATE)
        val other = file("other", "/tmp/b", JunkCategory.TEMP_FILE)

        val normalized = JunkScanRule.normalize(listOf(cache, other, duplicate))

        assertEquals(listOf(duplicate, other), normalized)
    }

    @Test
    fun cacheClassificationCannotSelectRetainedDuplicate() {
        val retained = file("keep", "/cache/original", JunkCategory.CACHE)
        val duplicate =
            file("copy", "/cache/duplicate", JunkCategory.DUPLICATE)
                .copy(duplicate = JunkDuplicateMetadataModel(hash = "same", originalPath = retained.path))
        assertEquals(listOf(duplicate), JunkScanRule.normalize(listOf(retained, duplicate)))
    }

    @Test
    fun `retained original collected after duplicates is filtered without shrinking the total`() {
        val copy =
            file("copy", "/tmp/copy", JunkCategory.DUPLICATE)
                .copy(duplicate = JunkDuplicateMetadataModel(hash = "same", originalPath = "/tmp/original"))
        val retained = file("original", "/tmp/original", JunkCategory.APK).copy(sizeBytes = 20L)

        val afterDuplicates = JunkScanRule.normalize(listOf(copy))
        val afterOtherScanners = JunkScanRule.normalize(listOf(copy, retained))

        assertEquals(afterDuplicates.sumOf { it.sizeBytes }, afterOtherScanners.sumOf { it.sizeBytes })
        assertEquals(listOf(copy), afterOtherScanners)
    }

    @Test
    fun `retained original counted before duplicates shrinks the running total`() {
        val retained = file("original", "/tmp/original", JunkCategory.APK).copy(sizeBytes = 20L)
        val copy =
            file("copy", "/tmp/copy", JunkCategory.DUPLICATE)
                .copy(duplicate = JunkDuplicateMetadataModel(hash = "same", originalPath = "/tmp/original"))

        val beforeDuplicates = JunkScanRule.normalize(listOf(retained))
        val afterDuplicates = JunkScanRule.normalize(listOf(retained, copy))

        assertEquals(20L, beforeDuplicates.sumOf { it.sizeBytes })
        assertEquals(10L, afterDuplicates.sumOf { it.sizeBytes })
    }

    private fun file(
        id: String,
        path: String,
        category: JunkCategory,
    ) = JunkFileModel(
        id = id,
        path = path,
        name = path.substringAfterLast('/'),
        sizeBytes = 10L,
        category = category,
        modifiedMillis = 0L,
    )
}
