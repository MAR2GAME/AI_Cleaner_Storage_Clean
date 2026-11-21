package com.aicleanerstorage.spaceclean.domain.toolbox.battery

import com.aicleanerstorage.spaceclean.domain.battery.usecase.SyncBackgroundBatterySamplingUseCase
import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncBackgroundBatterySamplingUseCaseTest {
    @Test
    fun `activate persists preference and syncs runtime`() {
        val preferences = InMemorySettingsRepository()
        val calls = mutableListOf<String>()

        controller(preferences, calls).activate()

        assertTrue(preferences.isBackgroundBatterySamplingEnabled())
        assertEquals(listOf("sync"), calls)
    }

    @Test
    fun `reconcile syncs runtime without touching the preference`() {
        val preferences = InMemorySettingsRepository()
        val calls = mutableListOf<String>()

        controller(preferences, calls).reconcile()

        assertEquals(listOf("sync"), calls)
        assertEquals(false, preferences.isBackgroundBatterySamplingEnabled())
    }

    private fun controller(
        preferences: InMemorySettingsRepository,
        calls: MutableList<String>,
    ) = SyncBackgroundBatterySamplingUseCase(
        settingsRepository = preferences,
        syncRuntime = { calls += "sync" },
    )
}
