package com.aicleanerstorage.spaceclean.data.local.battery

import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
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

internal class BatteryHistorySampler(
    private val deviceInfoRepository: DeviceInfoRepository,
    private val historyRepository: BatteryHistoryRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val nowMillis: () -> Long = System::currentTimeMillis,
    val sampleIntervalMillis: Long = BatteryHistoryRepository.SAMPLE_INTERVAL_MILLIS,
) : BatteryHistoryRecorder {
    init {
        require(sampleIntervalMillis > 0L)
    }

    private val lock = Any()
    private val owners = mutableSetOf<BatteryHistoryOwner>()
    private var samplingJob: Job? = null
    private var samplingScope: CoroutineScope? = null

    override fun start(owner: BatteryHistoryOwner) {
        synchronized(lock) {
            owners += owner
            if (samplingJob?.isActive == true) return
            historyRepository.loadRecent(nowMillis())
            val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
            samplingScope = scope
            samplingJob =
                scope.launch(ioDispatcher) {
                    while (isActive) {
                        sampleOnce(force = false)
                        delay(sampleIntervalMillis)
                    }
                }
        }
    }

    override fun stop(owner: BatteryHistoryOwner) {
        synchronized(lock) {
            owners -= owner
            if (owners.isNotEmpty()) return
            samplingJob?.cancel()
            samplingJob = null
            samplingScope?.cancel()
            samplingScope = null
        }
    }

    override fun sampleOnce(force: Boolean): BatteryHistorySampleModel? =
        synchronized(lock) {
            val now = nowMillis()
            val latestSample =
                historyRepository.latestSample()
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
