package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCandidateModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class JunkClassificationRuleTest {
    private val now = 1_700_000_000_000L
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun `cache source is junk regardless of size`() {
        val candidate = candidate(path = "/data/cache/empty", size = 0L, source = JunkSource.AppCache)
        assertEquals(JunkCategory.CACHE, JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `duplicate apk names are junk`() {
        val candidate = candidate(path = "/storage/Download/app (1).apk", size = 2_048L, modified = now)
        assertEquals(JunkCategory.APK, JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `old downloaded apk is junk after seven days`() {
        val candidate =
            candidate(
                path = "/storage/emulated/0/Download/old.apk",
                size = 2_048L,
                modified = now - 8 * day,
            )
        assertEquals(JunkCategory.APK, JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `fresh apk outside download is not junk`() {
        val candidate =
            candidate(
                path = "/storage/emulated/0/Documents/app.apk",
                size = 2_048L,
                modified = now - day,
            )
        assertNull(JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `system apk paths are skipped`() {
        val candidate = candidate(path = "/system/app/System.apk", size = 2_048L, modified = now - 40 * day)
        assertNull(JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `small apk is not junk`() {
        val candidate = candidate(path = "/storage/Documents/small.apk", size = 10L, modified = now - 40 * day)
        assertNull(JunkClassificationRule.categoryOf(candidate, now))
    }

    @Test
    fun `temp extensions and prefixes are junk above one kilobyte`() {
        assertEquals(
            JunkCategory.TEMP_FILE,
            JunkClassificationRule.categoryOf(candidate(path = "/storage/a.tmp", size = 1_024L), now),
        )
        assertEquals(
            JunkCategory.TEMP_FILE,
            JunkClassificationRule.categoryOf(candidate(path = "/storage/temp_log.txt", size = 1_024L), now),
        )
    }

    @Test
    fun `small temp file is not junk`() {
        assertNull(JunkClassificationRule.categoryOf(candidate(path = "/storage/a.tmp", size = 1_023L), now))
    }

    @Test
    fun publicResiduesAndAdCachesExcludeOrdinaryDownloads() {
        fun classify(path: String) = JunkClassificationRule.categoryOf(candidate(path, 2048L, now), now)
        assertNull(classify("/storage/Download/合同.pdf"))
        assertNull(classify("/storage/Download/adventure.jpg"))
        assertEquals(JunkCategory.CACHE, classify("/storage/Download/adcache/image.bin"))
        assertEquals(JunkCategory.RESIDUAL, classify("/storage/Documents/.trash/expired.bin"))
        assertEquals(JunkCategory.RESIDUAL, classify("/storage/Documents/expired.bak2"))
    }

    private fun candidate(
        path: String,
        size: Long,
        modified: Long = now,
        source: JunkSource = JunkSource.PublicStorage,
    ) = JunkCandidateModel(
        path = path,
        name = path.substringAfterLast('/'),
        sizeBytes = size,
        modifiedMillis = modified,
        source = source,
    )
}
