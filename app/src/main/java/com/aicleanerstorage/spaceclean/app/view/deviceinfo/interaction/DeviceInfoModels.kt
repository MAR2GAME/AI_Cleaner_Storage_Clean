package com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceCpuInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceSensorInfoModel
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

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
    val battery: BatteryInfoModel = EMPTY_BATTERY_INFO,
    val memory: CapacityInfoModel = EMPTY_MEMORY_INFO,
    val storage: CapacityInfoModel = EMPTY_STORAGE_INFO,
    val hardware: DeviceHardwareInfoModel = EMPTY_HARDWARE_INFO,
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
    val error: UiText? = null,
    val isScanning: Boolean = true,
    val showStopDialog: Boolean = false,
) {
    val batteryCapacityText: String
        get() = battery.levelPercent.takeIf { it >= 0 }?.let { "$it%" } ?: "--"

    val batteryAvailableTimeMinutes: Int?
        get() = battery.availableTimeMinutes

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
internal val EMPTY_BATTERY_INFO = BatteryInfoModel(-1)
internal val EMPTY_MEMORY_INFO = CapacityInfoModel(0L, 0L)
internal val EMPTY_STORAGE_INFO = CapacityInfoModel(0L, 0L)
internal val EMPTY_HARDWARE_INFO =
    DeviceHardwareInfoModel(
        model = UNKNOWN,
        androidVersion = "Android --",
        screenSize = "--",
        screenDensity = "--",
        multiTouchSupported = false,
        sensors =
            DeviceSensorInfoModel(
                accelerometer = false,
                magneticField = false,
                orientation = false,
                gyroscope = false,
                light = false,
                proximity = false,
                ambientTemperature = false,
            ),
        cpu =
            DeviceCpuInfoModel(
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
    val displayTemperature =
        if (normalizedUnit == TEMPERATURE_UNIT_FAHRENHEIT) {
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

fun currentBatteryCapacityMah(battery: BatteryInfoModel): Int? {
    val totalCapacity = battery.capacity.takeIf { it > 0 } ?: return null
    val level = battery.levelPercent.takeIf { it >= 0 } ?: return null
    return (totalCapacity * level / 100f).roundToInt()
}

fun String.orPlaceholder(): String = takeIf { isNotBlank() && this != UNKNOWN } ?: "--"

internal fun String.normalizeTemperatureUnit(): String =
    if (equals(TEMPERATURE_UNIT_FAHRENHEIT, ignoreCase = true)) {
        TEMPERATURE_UNIT_FAHRENHEIT
    } else {
        TEMPERATURE_UNIT_CELSIUS
    }

internal fun celsiusToFahrenheit(value: Float): Float = value * 9f / 5f + 32f

internal const val TEMPERATURE_UNIT_FAHRENHEIT = "F"
