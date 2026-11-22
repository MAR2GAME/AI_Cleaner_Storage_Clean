package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 璁惧淇℃伅/鐢垫睜椤靛叡鐢ㄧ殑鐘舵€佺紪鎺掑櫒銆?
 *
 * 璐熻矗鍔犺浇璁惧淇℃伅銆佹帹杩涙壂鎻忓姩鐢汇€佸惎鍋滅數姹犻噰鏍峰拰鎶婄粨鏋滃啓鍥?UiState锛?
 * 椤甸潰鍙€氳繃 ViewModel 璋冪敤瀹冿紝涓嶇洿鎺ユ寔鏈夊崗绋嬫垨浠撳簱銆?
 */
internal class DeviceInfoStateController(
    private val repository: DeviceInfoRepository,
    private val batteryHistoryRepository: BatteryHistoryRepository,
    private val batteryHistorySampler: BatteryHistoryRecorder,
    private val settingsRepository: AppSettingsRepository,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    initialMode: DeviceInfoMode,
    private val currentState: () -> DeviceInfoUiState,
    private val setState: ((DeviceInfoUiState) -> DeviceInfoUiState) -> Unit,
    private val nowMillis: () -> Long = System::currentTimeMillis,
    private val maxSampleCount: Int = BatteryHistoryRepository.MAX_SAMPLES,
) {
    private var historyCollectionJob: Job? = null
    private var scanJob: Job? = null
    private var scanDelayFinished = false

    init {
        load(initialMode)
    }

    fun load(mode: DeviceInfoMode) {
        startScanDelay()
        setState {
            it.copy(
                mode = mode,
                isLoading = true,
                cpuUsagePercent = null,
                error = null,
                showStopDialog = false,
            )
        }
        if (mode == DeviceInfoMode.Battery) {
            startHistoryCollection()
        } else {
            stopBatterySampling()
        }
        launchLoad(mode)
    }

    private fun launchLoad(mode: DeviceInfoMode) {
        scope.launch(ioDispatcher) {
            val timestamp = nowMillis()
            val previousState = currentState()
            runSuspendCatching {
                buildState(
                    mode = mode,
                    previousState = previousState,
                    timestampMillis = timestamp,
                )
            }.onSuccess { loaded ->
                setState { flowState ->
                    loaded.copy(
                        isScanning = flowState.isScanning,
                        showStopDialog = flowState.showStopDialog,
                    )
                }
                finishScanIfReady()
            }.onFailure { error ->
                setState {
                    it.copy(
                        isLoading = false,
                        error = UiText.Res(R.string.device_info_load_failed),
                    )
                }
                finishScanIfReady()
            }
        }
    }

    fun showStopDialog() {
        if (!currentState().isScanning) return
        scanJob?.cancel()
        scanDelayFinished = false
        setState { it.copy(showStopDialog = true) }
    }

    fun resumeScan() {
        startScanDelay()
    }

    fun dismissStopDialog() {
        setState { it.copy(showStopDialog = false) }
    }

    fun startBatterySampling() {
        startHistoryCollection()
        scope.launch(ioDispatcher) {
            val timestamp = nowMillis()
            applyHistorySamples(batteryHistoryRepository.loadRecent(timestamp), timestamp)
            val forceSample = shouldForceBatterySample(timestamp)
            sampleBatterySnapshot(forceSample = forceSample)
            batteryHistorySampler.start(BatteryHistoryOwner.BatteryPage)
        }
    }

    fun stopBatterySampling() {
        batteryHistorySampler.stop(BatteryHistoryOwner.BatteryPage)
        historyCollectionJob?.cancel()
        historyCollectionJob = null
    }

    fun selectCurrentRange(range: BatteryCurrentRange) {
        setState { it.copy(selectedCurrentRange = range) }
    }

    fun close() {
        scanJob?.cancel()
        stopBatterySampling()
    }

    private fun startScanDelay() {
        scanJob?.cancel()
        scanDelayFinished = false
        setState { it.copy(isScanning = true, showStopDialog = false) }
        scanJob =
            scope.launch {
                delay(INFO_SCAN_DURATION_MILLIS)
                scanDelayFinished = true
                finishScanIfReady()
            }
    }

    private fun finishScanIfReady() {
        if (scanDelayFinished && !currentState().isLoading) {
            setState { it.copy(isScanning = false, showStopDialog = false) }
        }
    }

    private suspend fun buildState(
        mode: DeviceInfoMode,
        previousState: DeviceInfoUiState,
        timestampMillis: Long,
    ): DeviceInfoUiState {
        val battery = repository.batteryInfo()
        val memory = repository.memoryInfo()
        val storage = repository.internalStorageInfo()
        val hardware = repository.hardwareInfo()
        val cpuTemperatureC = repository.cpuTemperatureC()
        val currentNow = repository.batteryCurrentNowMa().normalizedBatteryCurrent()
        val currentAverage = repository.batteryCurrentAverageMa().normalizedBatteryCurrent()
        val historySamples = batteryHistoryRepository.loadRecent(timestampMillis)

        return DeviceInfoUiState(
            mode = mode,
            battery = battery,
            memory = memory,
            storage = storage,
            hardware = hardware,
            cpuUsagePercent = repository.readCpuUsagePercent(previousState.cpuUsagePercent),
            cpuTemperatureC = cpuTemperatureC ?: previousState.cpuTemperatureC,
            currentNow = currentNow,
            currentAverage = currentAverage,
            selectedCurrentRange = previousState.selectedCurrentRange,
            currentSamples = historySamples.toCurrentSamples(timestampMillis, maxSampleCount),
            temperatureSamples = historySamples.toTemperatureSamples(timestampMillis),
            latestSampleTimestampMillis = timestampMillis,
            temperatureUnit = readTemperatureUnit(),
            isLoading = false,
            error = null,
        ).withHistorySamples(historySamples, timestampMillis, maxSampleCount)
    }

    private suspend fun sampleBatterySnapshot(forceSample: Boolean) {
        if (forceSample) {
            batteryHistorySampler.sampleOnce(force = true)
        }
        val timestamp = nowMillis()
        val battery = runSuspendCatching { repository.batteryInfo() }.getOrNull()
        val currentNow = runSuspendCatching { repository.batteryCurrentNowMa().normalizedBatteryCurrent() }.getOrNull()
        val currentAverage = runSuspendCatching { repository.batteryCurrentAverageMa().normalizedBatteryCurrent() }.getOrNull()
        val samples = batteryHistoryRepository.loadRecent(timestamp)

        setState { state ->
            state
                .copy(
                    battery = battery ?: state.battery,
                    currentNow = currentNow ?: state.currentNow,
                    currentAverage = currentAverage ?: state.currentAverage,
                    temperatureUnit = readTemperatureUnit(),
                    isLoading = false,
                ).withHistorySamples(samples, timestamp, maxSampleCount)
        }
    }

    private fun startHistoryCollection() {
        if (historyCollectionJob?.isActive == true) return
        historyCollectionJob =
            scope.launch(ioDispatcher) {
                batteryHistoryRepository.observeSamples().collect { samples ->
                    applyHistorySamples(samples, nowMillis())
                }
            }
    }

    private fun applyHistorySamples(
        samples: List<BatteryHistorySampleModel>,
        timestampMillis: Long,
    ) {
        setState { state ->
            state
                .copy(temperatureUnit = readTemperatureUnit())
                .withHistorySamples(samples, timestampMillis, maxSampleCount)
        }
    }

    private fun readTemperatureUnit(): String = settingsRepository.readTemperatureUnit().normalizeTemperatureUnit()

    private fun shouldForceBatterySample(timestampMillis: Long): Boolean {
        val latestSample =
            batteryHistoryRepository
                .loadRecent(timestampMillis)
                .lastOrNull()
                ?: batteryHistoryRepository.loadRecent(timestampMillis).lastOrNull()
                ?: return true
        return timestampMillis - latestSample.timestampMillis !in 0L..BATTERY_HISTORY_FRESH_SAMPLE_MILLIS
    }
}

private fun DeviceInfoUiState.withHistorySamples(
    samples: List<BatteryHistorySampleModel>,
    timestampMillis: Long,
    maxSampleCount: Int,
): DeviceInfoUiState {
    val recentSamples = samples.recentBatteryHistory(timestampMillis)
    val latestSample = recentSamples.lastOrNull()
    val freshLatestSample =
        latestSample?.takeIf { sample ->
            timestampMillis - sample.timestampMillis in 0L..BATTERY_HISTORY_FRESH_SAMPLE_MILLIS
        }
    return copy(
        battery =
            freshLatestSample?.let { latest ->
                battery.copy(temperature = latest.temperatureC)
            } ?: battery,
        currentNow = freshLatestSample?.currentMa?.normalizedBatteryCurrent() ?: currentNow,
        currentSamples = recentSamples.toCurrentSamples(timestampMillis, maxSampleCount),
        temperatureSamples = recentSamples.toTemperatureSamples(timestampMillis),
        latestSampleTimestampMillis = latestSample?.timestampMillis ?: timestampMillis,
    )
}

private fun List<BatteryHistorySampleModel>.toCurrentSamples(
    timestampMillis: Long,
    maxSampleCount: Int,
): List<BatteryCurrentSample> {
    val currentSamples =
        recentBatteryHistory(timestampMillis)
            .asSequence()
            .mapNotNull { sample ->
                sample.currentMa
                    ?.takeIf { it.isFiniteValue() }
                    ?.let { BatteryCurrentSample(sample.timestampMillis, abs(it)) }
            }.toList()
    return currentSamples.takeLast(maxSampleCount)
}

private fun List<BatteryHistorySampleModel>.toTemperatureSamples(timestampMillis: Long): List<BatteryTemperatureSample> =
    recentBatteryHistory(timestampMillis)
        .asSequence()
        .filter { it.timestampMillis >= timestampMillis - TEMPERATURE_WINDOW_MILLIS }
        .filter { it.temperatureC.isFiniteValue() }
        .map { BatteryTemperatureSample(it.timestampMillis, it.temperatureC) }
        .toList()

private fun List<BatteryHistorySampleModel>.recentBatteryHistory(timestampMillis: Long): List<BatteryHistorySampleModel> {
    val cutoff = timestampMillis - BatteryCurrentRange.TwentyFourHours.durationMillis
    val latestAllowed = timestampMillis + BatteryHistoryRepository.FUTURE_TOLERANCE_MILLIS
    return asSequence()
        .filter { it.timestampMillis in cutoff..latestAllowed }
        .sortedBy { it.timestampMillis }
        .toList()
}

private const val INFO_SCAN_DURATION_MILLIS = 1_500L
private const val BATTERY_HISTORY_FRESH_SAMPLE_MILLIS = BatteryHistoryRepository.SAMPLE_INTERVAL_MILLIS + 5_000L
