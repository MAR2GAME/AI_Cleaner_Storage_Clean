package com.aicleanerstorage.cleanerkit.clean.internal

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CleanSessionResultTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun removingConfirmedFilesUpdatesRemainingScanUntilEmpty() {
        val retained = folder.newFile("retained")
        val removed = JunkFile("1", folder.root.resolve("removed").path, "removed", 8, JunkCategory.CACHE)
        val remaining = JunkFile("2", retained.path, "retained", 3, JunkCategory.TEMP_FILE)
        val scanned = listOf(removed, remaining)
        val store = InMemoryCleanSessionStore()
        store.setScanResult(ScanResult(scanned))

        store.removeCleanedFiles(listOf(removed))
        assertEquals(ScanProgress(percent = 100f, foundCount = 1, foundSize = 3), store.scanProgress.value)
        assertEquals(listOf(remaining), store.scanResult.value?.junkFiles)

        store.removeCleanedFiles(listOf(remaining))
        assertEquals(ScanProgress.IDLE, store.scanProgress.value)
        assertEquals(0, store.scanResult.value?.totalCount)
    }
}
