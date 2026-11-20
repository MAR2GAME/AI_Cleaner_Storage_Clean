package com.aicleanerstorage.spaceclean.domain.usecase

import com.aicleanerstorage.spaceclean.domain.rule.BatteryHistoryRule
import com.aicleanerstorage.spaceclean.domain.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.model.BatteryHistorySampleModel
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SampleBatteryHistoryUseCase(
    private val deviceInfoRepository: DeviceInfoRepository,
    private val historyRepository: BatteryHistoryRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val nowMillis: () -> Long = System::currentTimeMillis,
    val sampleIntervalMillis: Long = BatteryHistoryRule.SAMPLE_INTERVAL_MILLIS,
) {
    private val lock = Any()
    private val owners = mutableSetOf<BatteryHistoryOwner>()
    private var samplingJob: Job? = null
    private var samplingScope: CoroutineScope? = null

    fun start(owner: BatteryHistoryOwner) {
        synchronized(lock) {
            owners += owner
            if (samplingJob?.isActive == true) return
            historyRepository.loadRecent(nowMillis())
            val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
            samplingScope = scope
            samplingJob =
                scope.launch(ioDispatcher) {
                    while (isActive) {
                        sampleOnce()
                        delay(sampleIntervalMillis)
                    }
                }
        }
    }

    fun stop(owner: BatteryHistoryOwner) {
        synchronized(lock) {
            owners -= owner
            if (owners.isNotEmpty()) return
            samplingJob?.cancel()
            samplingJob = null
            samplingScope?.cancel()
            samplingScope = null
        }
    }

    fun sampleOnce(force: Boolean = false): BatteryHistorySampleModel? =
        synchronized(lock) {
            val now = nowMillis()
            val latestSample =
                historyRepository.samples.value.lastOrNull()
                    ?: historyRepository.loadRecent(now).lastOrNull()
            if (!force && latestSample != null && now - latestSample.timestampMillis in 0 until sampleIntervalMillis) {
                return@synchronized null
            }

            val battery =
                runCatching { deviceInfoRepository.batteryInfo() }.getOrNull()
                    ?: return@synchronized null
            if (!battery.temperature.isFiniteValue()) {
                return@synchronized null
            }
            val currentNow =
                runCatching { deviceInfoRepository.batteryCurrentNowMa() }
                    .getOrNull()
                    ?.takeIf { it.isFiniteValue() }
            val sample =
                BatteryHistorySampleModel(
                    timestampMillis = now,
                    currentMa = currentNow,
                    temperatureC = battery.temperature,
                )
            historyRepository.append(sample, now)
            sample
        }
}

private fun Float.isFiniteValue(): Boolean = !isNaN() && !isInfinite()

