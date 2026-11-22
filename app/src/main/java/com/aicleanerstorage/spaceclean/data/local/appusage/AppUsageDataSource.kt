package com.aicleanerstorage.spaceclean.data.local.appusage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

internal data class UsageTotals(
    val packageName: String,
    val foregroundMillis: Long,
    val launches: Int,
)

internal object AppUsageDataSource {
    @Volatile
    private var usageAccessGranted: Boolean? = null

    fun hasUsageAccess(
        context: Context,
        refresh: Boolean = false,
    ): Boolean {
        if (!refresh) usageAccessGranted?.let { return it }
        val granted = AppPermission.UsageAccess.isGranted(context)
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

    fun foregroundPackage(context: Context): String? {
        if (!hasUsageAccess(context)) return null
        val manager =
            runCatching { context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }
                .getOrNull() ?: return null
        val now = System.currentTimeMillis()
        val events =
            runCatching {
                manager.queryEvents((now - FOREGROUND_EVENT_LOOKBACK_MILLIS).coerceAtLeast(0L), now)
            }.getOrNull()
        val event = UsageEvents.Event()
        var foreground: String? = null
        if (events != null) {
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                val packageName = event.packageName ?: continue
                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND,
                    UsageEvents.Event.ACTIVITY_RESUMED,
                    -> foreground = packageName

                    UsageEvents.Event.MOVE_TO_BACKGROUND,
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED,
                    -> if (foreground == packageName) foreground = null
                }
            }
        }
        return foreground ?: foregroundPackageFromStats(manager, now)
    }

    private fun foregroundPackageFromStats(
        manager: UsageStatsManager,
        now: Long,
    ): String? =
        runCatching {
            manager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                (now - FOREGROUND_STATS_LOOKBACK_MILLIS).coerceAtLeast(0L),
                now,
            )
        }.getOrNull()
            ?.maxByOrNull { it.lastTimeUsed }
            ?.packageName

    suspend fun readUsage(
        context: Context,
        startMillis: Long,
        endMillis: Long,
    ): List<UsageTotals> =
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
    private const val FOREGROUND_EVENT_LOOKBACK_MILLIS = 3_000L
    private const val FOREGROUND_STATS_LOOKBACK_MILLIS = 10_000L
}
