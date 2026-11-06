package com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingPreferences

class BackgroundBatterySamplingController(
    private val preferences: BackgroundBatterySamplingPreferences,
    private val hasNotificationPermission: () -> Boolean,
    private val enableServiceSampling: () -> Unit,
    private val disableServiceSampling: () -> Unit,
) {
    fun activate() {
        preferences.enableBackgroundSampling()
        reconcile()
    }

    fun reconcile() {
        if (preferences.isBackgroundSamplingEnabled() && hasNotificationPermission()) {
            enableServiceSampling()
        } else {
            disableServiceSampling()
        }
    }
}

