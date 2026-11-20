package com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.devicekit.model.DeviceCpuInfo
import com.aicleanerstorage.devicekit.model.DeviceHardwareInfo
import com.aicleanerstorage.devicekit.model.DeviceSensorInfo
import com.aicleanerstorage.devicekit.model.BatteryInfo
import com.aicleanerstorage.devicekit.model.MemoryInfo
import com.aicleanerstorage.devicekit.model.StorageInfo
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistorySampleModel
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.abs

enum class DeviceInfoMode {
    Device,
    Battery,
}

enum class BatteryCurrentRange(
    @StringRes val labelRes: Int,
    val durationMillis: Long,
) {
    OneMinute(R.string.battery_range_1_min, 60_000L),
    OneHour(R.string.battery_range_1_hour, 60L * 60L * 1000L),
    TwentyFourHours(R.string.battery_range_24_hours, 24L * 60L * 60L * 1000L),
}

data class BatteryCurrentSample(
    val timestampMillis: Long,
    val currentMa: Float,
)

data class BatteryTemperatureSample(
    val timestampMillis: Long,
    val temperatureC: Float,
)

data class DeviceInfoUiState(
    val mode: DeviceInfoMode = DeviceInfoMode.Device,
    val battery: BatteryInfo = EMPTY_BATTERY_INFO,
    val memory: MemoryInfo = EMPTY_MEMORY_INFO,
    val storage: StorageInfo = EMPTY_STORAGE_INFO,
    val hardware: DeviceHardwareInfo = EMPTY_HARDWARE_INFO,
    val cpuUsagePercent: Int? = null,
    val cpuTemperatureC: Float? = null,
    val currentNow: Float? = null,
    val currentAverage: Float? = null,
    val selectedCurrentRange: BatteryCurrentRange = BatteryCurrentRange.OneMinute,
    val currentSamples: List<BatteryCurrentSample> = emptyList(),
    val temperatureSamples: List<BatteryTemperatureSample> = emptyList(),
    val latestSampleTimestampMillis: Long = 0L,
    val temperatureUnit: String = TEMPERATURE_UNIT_CELSIUS,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isScanning: Boolean = true,
    val showStopDialog: Boolean = false,
) {
    val batteryCapacityText: String
        get() = battery.levelPercent.takeIf { it >= 0 }?.let { "$it%" } ?: "--"

    val batteryLifeText: String
        get() = battery.availableTime.takeIf { it.isNotBlank() && it != UNKNOWN } ?: "--"

    val selectedCurrentSamples: List<BatteryCurrentSample>
        get() =
            filterCurrentSamples(
                samples = currentSamples,
                selectedRange = selectedCurrentRange,
                timestampMillis = latestSampleTimestampMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
            )

    val selectedCurrentAverage: Float?
        get() = averageCurrent(selectedCurrentSamples, currentAverage)

    val selectedTemperatureSamples: List<BatteryTemperatureSample>
        get() =
            filterTemperatureSamples(
                samples = temperatureSamples,
                timestampMillis = latestSampleTimestampMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
            )

    val selectedTemperatureAverageC: Float?
        get() = averageTemperature(selectedTemperatureSamples, battery.temperature)

    val displayCurrentNow: Float?
        get() = selectedCurrentSamples.lastOrNull()?.currentMa ?: currentNow

    val displayTemperatureC: Float
        get() = selectedTemperatureSamples.lastOrNull()?.temperatureC ?: battery.temperature
}

internal fun filterCurrentSamples(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    timestampMillis: Long,
): List<BatteryCurrentSample> {
    val cutoff = timestampMillis - selectedRange.durationMillis
    return samples
        .asSequence()
        .filter { it.timestampMillis in cutoff..timestampMillis }
        .sortedBy { it.timestampMillis }
        .toList()
}

internal fun filterTemperatureSamples(
    samples: List<BatteryTemperatureSample>,
    timestampMillis: Long,
): List<BatteryTemperatureSample> {
    val cutoff = timestampMillis - TEMPERATURE_WINDOW_MILLIS
    return samples
        .asSequence()
        .filter { it.timestampMillis in cutoff..timestampMillis }
        .sortedBy { it.timestampMillis }
        .toList()
}

internal fun averageCurrent(
    samples: List<BatteryCurrentSample>,
    fallback: Float?,
): Float? =
    samples
        .takeIf { it.isNotEmpty() }
        ?.map { abs(it.currentMa) }
        ?.average()
        ?.toFloat()
        ?: fallback.normalizedBatteryCurrent()

internal fun averageTemperature(
    samples: List<BatteryTemperatureSample>,
    fallback: Float?,
): Float? =
    samples
        .takeIf { it.isNotEmpty() }
        ?.map { it.temperatureC }
        ?.average()
        ?.toFloat()
        ?: fallback

internal const val UNKNOWN = "Unknown"
internal const val TEMPERATURE_UNIT_CELSIUS = "C"
internal val EMPTY_BATTERY_INFO = BatteryInfo(-1, UNKNOWN, 0f, -1, UNKNOWN, 0, UNKNOWN)
internal val EMPTY_MEMORY_INFO = MemoryInfo(0L, 0L)
internal val EMPTY_STORAGE_INFO = StorageInfo(0L, 0L)
internal val EMPTY_HARDWARE_INFO =
    DeviceHardwareInfo(
        model = UNKNOWN,
        androidVersion = "Android --",
        screenSize = "--",
        screenDensity = "--",
        multiTouchSupported = false,
        sensors =
            DeviceSensorInfo(
                accelerometer = false,
                magneticField = false,
                orientation = false,
                gyroscope = false,
                light = false,
                proximity = false,
                ambientTemperature = false,
            ),
        cpu =
            DeviceCpuInfo(
                hardware = UNKNOWN,
                model = UNKNOWN,
                cores = 0,
                maxFrequency = UNKNOWN,
            ),
    )

internal fun Float?.normalizedBatteryCurrent(): Float? = this?.takeIf { it.isFiniteValue() }?.let { abs(it) }

internal const val TEMPERATURE_WINDOW_MILLIS = 60_000L
internal fun Float.isFiniteValue(): Boolean = !isNaN() && !isInfinite()

fun formatBatteryTemperature(
    tempC: Float?,
    unit: String = TEMPERATURE_UNIT_CELSIUS,
    includeSpace: Boolean = true,
): String {
    val temperature = tempC?.takeIf { it.isFiniteValue() } ?: return "--"
    val normalizedUnit = unit.normalizeTemperatureUnit()
    val displayTemperature = if (normalizedUnit == TEMPERATURE_UNIT_FAHRENHEIT) {
        celsiusToFahrenheit(temperature)
    } else {
        temperature
    }
    val separator = if (includeSpace) " " else ""
    return String.format(Locale.US, "%.1f%s\u00B0%s", displayTemperature, separator, normalizedUnit)
}

fun displayBatteryTemperatureValue(
    tempC: Float,
    unit: String,
): Float =
    if (unit.normalizeTemperatureUnit() == TEMPERATURE_UNIT_FAHRENHEIT) {
        celsiusToFahrenheit(tempC)
    } else {
        tempC
    }

fun formatBatteryVoltage(voltageMilliVolts: Int): String =
    if (voltageMilliVolts > 0) {
        String.format(Locale.US, "%.1f V", voltageMilliVolts / 1000f)
    } else {
        "--"
    }

fun formatBatteryCurrent(currentMa: Float?): String =
    currentMa
        ?.takeIf { it.isFiniteValue() }
        ?.let { String.format(Locale.US, "%.2f mA", abs(it)) }
        ?: "--"

fun formatBatteryCapacityMah(capacityMah: Int?): String =
    capacityMah
        ?.takeIf { it > 0 }
        ?.let { "$it mAh" }
        ?: "--"

fun currentBatteryCapacityMah(battery: BatteryInfo): Int? {
    val totalCapacity = battery.capacity.takeIf { it > 0 } ?: return null
    val level = battery.levelPercent.takeIf { it >= 0 } ?: return null
    return (totalCapacity * level / 100f).roundToInt()
}

fun String.orPlaceholder(): String = takeIf { isNotBlank() && this != UNKNOWN } ?: "--"

class DeviceInfoStateController(
    private val repository: DeviceInfoRepository,
    private val batteryHistoryRepository: BatteryHistoryRepository,
    private val batteryHistorySampler: BatteryHistorySampler,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    initialMode: DeviceInfoMode,
    private val currentState: () -> DeviceInfoUiState,
    private val changeState: ((DeviceInfoUiState) -> DeviceInfoUiState) -> Unit,
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
        changeState {
            it.copy(
                mode = mode,
                isLoading = true,
                cpuUsagePercent = null,
                errorMessage = null,
                showStopDialog = false,
            )
        }
        if (mode == DeviceInfoMode.Battery) {
            startHistoryCollection()
        } else {
            stopBatterySampling()
        }
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
                changeState { flowState ->
                    loaded.copy(
                        isScanning = flowState.isScanning,
                        showStopDialog = flowState.showStopDialog,
                    )
                }
                finishScanIfReady()
            }.onFailure { error ->
                changeState {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message,
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
        changeState { it.copy(showStopDialog = true) }
    }

    fun resumeScan() {
        startScanDelay()
    }

    fun dismissStopDialog() {
        changeState { it.copy(showStopDialog = false) }
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
        changeState { it.copy(selectedCurrentRange = range) }
    }

    fun close() {
        scanJob?.cancel()
        stopBatterySampling()
    }

    private fun startScanDelay() {
        scanJob?.cancel()
        scanDelayFinished = false
        changeState { it.copy(isScanning = true, showStopDialog = false) }
        scanJob =
            scope.launch {
                delay(INFO_SCAN_DURATION_MILLIS)
                scanDelayFinished = true
                finishScanIfReady()
            }
    }

    private fun finishScanIfReady() {
        if (scanDelayFinished && !currentState().isLoading) {
            changeState { it.copy(isScanning = false, showStopDialog = false) }
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
        val historySamples =
            if (mode == DeviceInfoMode.Battery) {
                batteryHistoryRepository.loadRecent(timestampMillis)
            } else {
                batteryHistoryRepository.samples.value
            }

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
            errorMessage = null,
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

        changeState { state ->
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
                batteryHistoryRepository.samples.collect { samples ->
                    applyHistorySamples(samples, nowMillis())
                }
            }
    }

    private fun applyHistorySamples(
        samples: List<BatteryHistorySampleModel>,
        timestampMillis: Long,
    ) {
        changeState { state ->
            state
                .copy(temperatureUnit = readTemperatureUnit())
                .withHistorySamples(samples, timestampMillis, maxSampleCount)
        }
    }

    private fun readTemperatureUnit(): String = settingsRepository.readTemperatureUnit().normalizeTemperatureUnit()

    private fun shouldForceBatterySample(timestampMillis: Long): Boolean {
        val latestSample =
            batteryHistoryRepository.samples.value
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

private fun String.normalizeTemperatureUnit(): String =
    if (equals(TEMPERATURE_UNIT_FAHRENHEIT, ignoreCase = true)) {
        TEMPERATURE_UNIT_FAHRENHEIT
    } else {
        TEMPERATURE_UNIT_CELSIUS
    }

private fun celsiusToFahrenheit(value: Float): Float = value * 9f / 5f + 32f

private const val TEMPERATURE_UNIT_FAHRENHEIT = "F"
private const val INFO_SCAN_DURATION_MILLIS = 1_500L
private const val BATTERY_HISTORY_FRESH_SAMPLE_MILLIS = BatteryHistoryRepository.SAMPLE_INTERVAL_MILLIS + 5_000L
