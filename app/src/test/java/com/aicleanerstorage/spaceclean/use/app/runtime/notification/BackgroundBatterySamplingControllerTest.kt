package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BackgroundBatterySamplingPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundBatterySamplingControllerTest {
    @Test
    fun `activate persists preference and enables service sampling when permission is granted`() {
        val preferences = RecordingPreferences()
        val calls = mutableListOf<String>()
        val controller = controller(preferences, hasPermission = true, calls)

        controller.activate()

        assertTrue(preferences.enabled)
        assertEquals(listOf("enable"), calls)
    }

    @Test
    fun `activate persists preference but disables service sampling without permission`() {
        val preferences = RecordingPreferences()
        val calls = mutableListOf<String>()
        val controller = controller(preferences, hasPermission = false, calls)

        controller.activate()

        assertTrue(preferences.enabled)
        assertEquals(listOf("disable"), calls)
    }

    @Test
    fun `reconcile enables only when preference and permission are both present`() {
        val calls = mutableListOf<String>()
        val disabledPreferences = RecordingPreferences(enabled = false)
        controller(disabledPreferences, hasPermission = true, calls).reconcile()

        val enabledPreferences = RecordingPreferences(enabled = true)
        controller(enabledPreferences, hasPermission = false, calls).reconcile()
        controller(enabledPreferences, hasPermission = true, calls).reconcile()

        assertEquals(listOf("disable", "disable", "enable"), calls)
    }

    private fun controller(
        preferences: BackgroundBatterySamplingPreferences,
        hasPermission: Boolean,
        calls: MutableList<String>,
    ) =
        BackgroundBatterySamplingController(
            preferences = preferences,
            hasNotificationPermission = { hasPermission },
            enableServiceSampling = { calls += "enable" },
            disableServiceSampling = { calls += "disable" },
        )
}

private class RecordingPreferences(
    var enabled: Boolean = false,
) : BackgroundBatterySamplingPreferences {
    override fun isBackgroundSamplingEnabled(): Boolean = enabled

    override fun enableBackgroundSampling() {
        enabled = true
    }
}
