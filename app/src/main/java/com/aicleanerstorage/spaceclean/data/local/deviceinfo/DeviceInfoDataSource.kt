package com.aicleanerstorage.spaceclean.data.local.deviceinfo

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryHealth
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryStatus
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import java.lang.reflect.Method

internal object DeviceInfoDataSource {
    fun getBatteryInfo(context: Context): BatteryInfoModel {
        val intent =
            getBatteryStatusIntent(context)
                ?: return BatteryInfoModel(-1, BatteryHealth.Unknown, 0f, -1, "Unknown", 0)
        val levelPercent = readLevelPercent(intent)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        return BatteryInfoModel(
            levelPercent = levelPercent,
            health = readHealth(intent),
            temperature = readTemperature(intent),
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown",
            capacity = getBatteryCapacity(context),
            availableTimeMinutes = estimateAvailableTime(levelPercent),
            status = readStatus(status),
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING,
        )
    }

    private fun readLevelPercent(intent: Intent): Int {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1)
        return if (level >= 0 && scale > 0) {
            (level * 100 / scale.toFloat()).toInt().coerceIn(0, 100)
        } else {
            -1
        }
    }

    private fun readHealth(intent: Intent): BatteryHealth =
        when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.Cold
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.Dead
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.Good
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.Overheat
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.Overvoltage
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.Failure
            else -> BatteryHealth.Unknown
        }

    private fun readTemperature(intent: Intent): Float {
        val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        return if (tempRaw >= 0) tempRaw / 10f else 0f
    }

    private fun readStatus(status: Int): BatteryStatus =
        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.Charging
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.Discharging
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.Full
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NotCharging
            else -> BatteryStatus.Unknown
        }

    fun getMemoryInfo(context: Context): CapacityInfoModel {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return CapacityInfoModel(memInfo.totalMem, memInfo.availMem)
    }

    private fun getBatteryStatusIntent(context: Context): Intent? =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                context.registerReceiver(
                    null,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                    Context.RECEIVER_NOT_EXPORTED,
                )
            } else {
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }
        } catch (_: Exception) {
            null
        }

    private fun getBatteryCapacity(context: Context): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return 0
        getBatteryCapacityByReflection(context)?.let { return it }
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val chargeCounter = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            val capacityPercent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if (chargeCounter == Long.MIN_VALUE || capacityPercent <= 0 || capacityPercent > 100) return 0
            val totalMicroAh = chargeCounter * 100 / capacityPercent
            (totalMicroAh / 1000).toInt()
        } catch (_: Exception) {
            0
        }
    }

    private fun getBatteryCapacityByReflection(context: Context): Int? =
        try {
            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
            val constructor = powerProfileClass.getConstructor(Context::class.java)
            val instance = constructor.newInstance(context)
            val method = getBatteryCapacityMethod(powerProfileClass)
            val capacity =
                if (method.parameterTypes.isEmpty()) {
                    method.invoke(instance) as Double
                } else {
                    method.invoke(instance, "battery.capacity") as Double
                }
            capacity.takeIf { it > 0.0 }?.toInt()
        } catch (_: Exception) {
            null
        }

    private fun getBatteryCapacityMethod(powerProfileClass: Class<*>): Method =
        try {
            powerProfileClass.getMethod("getBatteryCapacity")
        } catch (_: NoSuchMethodException) {
            powerProfileClass.getMethod("getAveragePower", String::class.java)
        }

    private fun estimateAvailableTime(levelPercent: Int): Int? {
        val validLevel = levelPercent.coerceIn(0, 100)
        if (validLevel <= 0) return 0
        return ((validLevel / ASSUMED_HOURLY_DRAIN) * 60f).toInt().coerceAtLeast(0)
    }

    private const val ASSUMED_HOURLY_DRAIN = 18.5f
}
