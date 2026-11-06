package com.aicleanerstorage.spaceclean.app.platform.storage.preferences

import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.DefaultCleanupHistoryStore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CleanupHistoryStoreTest {
    @Test
    fun `records cleanup count locally`() {
        var count = 0L
        val store = DefaultCleanupHistoryStore(readCount = { count }, writeCount = { count = it })

        assertFalse(store.hasCompletedCleanup())
        store.recordCompletedCleanup()
        store.recordCompletedCleanup()

        assertTrue(store.hasCompletedCleanup())
        assertEquals(2L, count)
    }
}

