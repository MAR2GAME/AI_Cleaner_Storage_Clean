package com.aicleanerstorage.spaceclean.data.appusage.internal

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.aicleanerstorage.spaceclean.base.permission.hasUsageAccess as hasSystemUsageAccess
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class UsageTotals(val packageName: String, val foregroundMillis: Long, val launches: Int)

internal object AppUsageDataSource {
    @Volatile
    private var usageAccessGranted: Boolean? = null

    fun hasUsageAccess(context: Context, refresh: Boolean = false): Boolean {
        if (!refresh) usageAccessGranted?.let { return it }
        val granted = hasSystemUsageAccess(context)
        usageAccessGranted = granted
        return granted
    }

    fun runningPackages(
        context: Context,
        packageNames: Set<String>,
    ): Set<String> {
        if (packageNames.isEmpty() || !hasUsageAccess(context)) return emptySet()

        val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val events =
            runCatching {
                manager.queryEvents((now - USAGE_STATE_LOOKBACK_MILLIS).coerceAtLeast(0L), now)
            }.getOrNull() ?: return emptySet()
        val event = UsageEvents.Event()
        val running = mutableSetOf<String>()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val packageName = event.packageName ?: continue
            if (packageName !in packageNames) continue
            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND,
                UsageEvents.Event.ACTIVITY_RESUMED,
                -> running.add(packageName)
                UsageEvents.Event.MOVE_TO_BACKGROUND,
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED,
                -> running.remove(packageName)
            }
        }
        return running
    }

    suspend fun readUsage(context: Context, startMillis: Long, endMillis: Long): List<UsageTotals> =
        withContext(Dispatchers.IO) {
            val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val durationByPackage = mutableMapOf<String, Long>()
            val launchCounts = mutableMapOf<String, Int>()
            val foregroundStartByPackage = mutableMapOf<String, Long>()
            val queryStartMillis = (startMillis - USAGE_STATE_LOOKBACK_MILLIS).coerceAtLeast(0L)
            val events =
                runCatching { manager.queryEvents(queryStartMillis, endMillis) }.getOrNull()
                    ?: return@withContext emptyList()
            val event = UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                val packageName = event.packageName ?: continue
                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND,
                    UsageEvents.Event.ACTIVITY_RESUMED,
                    -> {
                        val wasForeground = packageName in foregroundStartByPackage
                        if (!wasForeground) {
                            foregroundStartByPackage[packageName] = event.timeStamp.coerceAtLeast(startMillis)
                        }
                        if (!wasForeground && event.timeStamp >= startMillis) {
                            launchCounts[packageName] = (launchCounts[packageName] ?: 0) + 1
                        }
                    }

                    UsageEvents.Event.MOVE_TO_BACKGROUND,
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED,
                    -> {
                        val start = foregroundStartByPackage.remove(packageName) ?: continue
                        val end = event.timeStamp.coerceAtMost(endMillis)
                        if (end > start) {
                            durationByPackage[packageName] = (durationByPackage[packageName] ?: 0L) + (end - start)
                        }
                    }
                }
            }

            foregroundStartByPackage.forEach { (packageName, startTime) ->
                val clippedStart = max(startTime, startMillis)
                if (endMillis > clippedStart) {
                    durationByPackage[packageName] = (durationByPackage[packageName] ?: 0L) + (endMillis - clippedStart)
                }
            }

            durationByPackage.map { (pkg, duration) -> UsageTotals(pkg, duration, launchCounts[pkg] ?: 0) }
        }

    fun appLabel(
        context: Context,
        packageName: String,
    ): String =
        runCatching {
            context.packageManager
                .getApplicationLabel(
                    context.packageManager.getApplicationInfo(packageName, 0),
                ).toString()
        }.getOrDefault(packageName.substringAfterLast('.'))

    private const val USAGE_STATE_LOOKBACK_MILLIS = 31L * 24L * 60L * 60L * 1000L
}

