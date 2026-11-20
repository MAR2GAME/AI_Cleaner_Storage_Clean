package com.aicleanerstorage.spaceclean.data.local

import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.ScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CleanSessionResultTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun partialAuthorizationUpdatesRemainingScanUntilEmpty() {
        val retained = folder.newFile("retained")
        val removed = JunkFileModel("1", folder.root.resolve("removed").path, "removed", 8, JunkCategory.CACHE)
        val remaining = JunkFileModel("2", retained.path, "retained", 3, JunkCategory.TEMP_FILE)
        val scanned = listOf(removed, remaining)
        val store = InMemoryCleanSessionStore()
        store.setScanResult(ScanResultModel(scanned, 11, 2, scanned.groupBy { it.category }))

        val result = JunkFileDeleteHelper.finalizeAuthorizedDeletes(scanned.map { JunkDeleteOutcomeModel(it, false, 0) })
        assertEquals(listOf(removed), result.cleanedFiles)
        assertEquals(listOf(remaining), result.failedFiles)
        assertEquals(8L, result.freedSpace)
        store.removeCleanedFiles(result.cleanedFiles)
        assertEquals(ScanProgressModel(percent = 100f, foundCount = 1, foundSize = 3), store.scanProgress.value)
        assertEquals(listOf(remaining), store.scanResult.value?.junkFiles)

        store.removeCleanedFiles(listOf(remaining))
        assertEquals(ScanProgressModel.IDLE, store.scanProgress.value)
        assertEquals(0, store.scanResult.value?.totalCount)
    }
}
