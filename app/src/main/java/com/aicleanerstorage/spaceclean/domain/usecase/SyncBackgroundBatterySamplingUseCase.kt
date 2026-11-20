package com.aicleanerstorage.spaceclean.domain.usecase

import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository

class SyncBackgroundBatterySamplingUseCase(
    private val settingsRepository: SettingsRepository,
    private val hasNotificationPermission: () -> Boolean,
    private val enableServiceSampling: () -> Unit,
    private val disableServiceSampling: () -> Unit,
) {
    fun activate() {
        settingsRepository.enableBackgroundBatterySampling()
        reconcile()
    }

    fun reconcile() {
        if (settingsRepository.isBackgroundBatterySamplingEnabled() && hasNotificationPermission()) {
            enableServiceSampling()
        } else {
            disableServiceSampling()
        }
    }
}

