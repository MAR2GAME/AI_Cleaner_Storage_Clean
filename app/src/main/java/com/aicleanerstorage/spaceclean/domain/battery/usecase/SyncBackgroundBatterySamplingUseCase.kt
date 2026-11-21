package com.aicleanerstorage.spaceclean.domain.battery.usecase

import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository

class SyncBackgroundBatterySamplingUseCase(
    private val settingsRepository: SettingsRepository,
    private val syncRuntime: () -> Unit,
) {
    fun activate() {
        settingsRepository.enableBackgroundBatterySampling()
        reconcile()
    }

    fun reconcile() = syncRuntime()
}
