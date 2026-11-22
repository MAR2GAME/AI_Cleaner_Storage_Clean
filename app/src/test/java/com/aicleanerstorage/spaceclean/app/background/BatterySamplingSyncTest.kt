package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.testsupport.InMemoryAppSettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BatterySamplingSyncTest {
    @Test
    fun `activate persists preference and syncs runtime`() {
        val preferences = InMemoryAppSettingsRepository()
        val calls = mutableListOf<String>()

        controller(preferences, calls).activate()

        assertTrue(preferences.isBackgroundBatterySamplingEnabled())
        assertEquals(listOf("sync"), calls)
    }

    @Test
    fun `reconcile syncs runtime without touching the preference`() {
        val preferences = InMemoryAppSettingsRepository()
        val calls = mutableListOf<String>()

        controller(preferences, calls).reconcile()

        assertEquals(listOf("sync"), calls)
        assertEquals(false, preferences.isBackgroundBatterySamplingEnabled())
    }

    private fun controller(
        preferences: InMemoryAppSettingsRepository,
        calls: MutableList<String>,
    ) = BatterySamplingSync(
        settingsRepository = preferences,
        syncRuntime = { calls += "sync" },
    )
}
