package com.aicleanerstorage.spaceclean.app.storage

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingPreferences
import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.AppPreferences

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
