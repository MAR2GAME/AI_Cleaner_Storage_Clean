package com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery

interface BackgroundBatterySamplingPreferences {
    fun isBackgroundSamplingEnabled(): Boolean

    fun enableBackgroundSampling()
}


