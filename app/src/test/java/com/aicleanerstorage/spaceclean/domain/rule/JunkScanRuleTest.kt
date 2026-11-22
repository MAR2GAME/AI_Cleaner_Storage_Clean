package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
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
