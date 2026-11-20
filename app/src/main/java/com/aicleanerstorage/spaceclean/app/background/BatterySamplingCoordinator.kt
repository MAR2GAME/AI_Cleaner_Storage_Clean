package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.usecase.SampleBatteryHistoryUseCase


interface BatterySamplingServiceActions {
    fun syncSamplingState()

    fun enableSampling()

    fun disableSampling()
}

class BatterySamplingCoordinator(
    private val sampler: SampleBatteryHistoryUseCase,
    private val settingsRepository: SettingsRepository,
    private val hasNotificationPermission: () -> Boolean,
) : BatterySamplingServiceActions {
    override fun syncSamplingState() {
        if (settingsRepository.isBackgroundBatterySamplingEnabled() && hasNotificationPermission()) {
            enableSampling()
        } else {
            disableSampling()
        }
    }

    override fun enableSampling() {
        runCatching { sampler.start(BatteryHistoryOwner.Service) }
    }

    override fun disableSampling() {
        runCatching { sampler.stop(BatteryHistoryOwner.Service) }
    }
}

