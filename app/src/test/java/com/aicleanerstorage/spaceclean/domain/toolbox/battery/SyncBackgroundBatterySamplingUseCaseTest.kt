package com.aicleanerstorage.spaceclean.domain.toolbox.battery

import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository

import com.aicleanerstorage.spaceclean.domain.usecase.SyncBackgroundBatterySamplingUseCase

import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncBackgroundBatterySamplingUseCaseTest {
    @Test
    fun `activate persists preference and enables service sampling when permission is granted`() {
        val preferences = InMemorySettingsRepository()
        val calls = mutableListOf<String>()
        val controller = controller(preferences, hasPermission = true, calls)

        controller.activate()

        assertTrue(preferences.isBackgroundBatterySamplingEnabled())
        assertEquals(listOf("enable"), calls)
    }

    @Test
    fun `activate persists preference but disables service sampling without permission`() {
        val preferences = InMemorySettingsRepository()
        val calls = mutableListOf<String>()
        val controller = controller(preferences, hasPermission = false, calls)

        controller.activate()

        assertTrue(preferences.isBackgroundBatterySamplingEnabled())
        assertEquals(listOf("disable"), calls)
    }

    @Test
    fun `reconcile enables only when preference and permission are both present`() {
        val calls = mutableListOf<String>()
        val disabledPreferences = InMemorySettingsRepository(backgroundSampling = false)
        controller(disabledPreferences, hasPermission = true, calls).reconcile()

        val enabledPreferences = InMemorySettingsRepository(backgroundSampling = true)
        controller(enabledPreferences, hasPermission = false, calls).reconcile()
        controller(enabledPreferences, hasPermission = true, calls).reconcile()

        assertEquals(listOf("disable", "disable", "enable"), calls)
    }

    private fun controller(
        preferences: SettingsRepository,
        hasPermission: Boolean,
        calls: MutableList<String>,
    ) =
        SyncBackgroundBatterySamplingUseCase(
            settingsRepository = preferences,
            hasNotificationPermission = { hasPermission },
            enableServiceSampling = { calls += "enable" },
            disableServiceSampling = { calls += "disable" },
        )
}
