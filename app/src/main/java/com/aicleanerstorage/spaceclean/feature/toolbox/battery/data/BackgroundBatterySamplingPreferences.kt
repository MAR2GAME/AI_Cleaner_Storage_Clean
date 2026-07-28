package com.aicleanerstorage.spaceclean.feature.toolbox.battery.data

import com.aicleanerstorage.spaceclean.common.storage.preferences.AppPreferences

interface BackgroundBatterySamplingPreferences {
    fun isBackgroundSamplingEnabled(): Boolean

    fun enableBackgroundSampling()
}

class BackgroundBatterySamplingPreferencesImpl(
    appPreferences: AppPreferences,
) : BackgroundBatterySamplingPreferences {
    private val store = appPreferences.store

    override fun isBackgroundSamplingEnabled(): Boolean =
        store.getBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, false)

    override fun enableBackgroundSampling() {
        store.edit().putBoolean(KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED, true).apply()
    }
}

private const val KEY_BACKGROUND_BATTERY_SAMPLING_ENABLED = "background_battery_sampling_enabled"
