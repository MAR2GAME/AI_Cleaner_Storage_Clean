package com.aicleanerstorage.spaceclean.data.local.junkclean

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class JunkScanSessionStoreTest {
    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun removingConfirmedFilesUpdatesRemainingScanUntilEmpty() {
        val retained = folder.newFile("retained")
        val removed = JunkFileModel("1", folder.root.resolve("removed").path, "removed", 8, JunkCategory.CACHE)
        val remaining = JunkFileModel("2", retained.path, "retained", 3, JunkCategory.TEMP_FILE)
        val scanned = listOf(removed, remaining)
        val store = JunkScanSessionStore()
        store.setScanResult(JunkScanResultModel(scanned))

        store.removeCleanedFiles(listOf(removed))
        assertEquals(JunkScanProgressModel(percent = 100f, foundCount = 1, foundSize = 3), store.scanProgress.value)
        assertEquals(listOf(remaining), store.scanResult.value?.files)

        store.removeCleanedFiles(listOf(remaining))
        assertEquals(JunkScanProgressModel.IDLE, store.scanProgress.value)
        assertEquals(0, store.scanResult.value?.totalCount)
    }
}
