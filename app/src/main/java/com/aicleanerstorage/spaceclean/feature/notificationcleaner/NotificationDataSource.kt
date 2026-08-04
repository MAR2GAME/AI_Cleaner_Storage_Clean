package com.aicleanerstorage.spaceclean.feature.notificationcleaner

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Base64
import java.util.UUID

object NotificationDataSource {
    private const val PREFS = "notification_blocker"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_BLOCKED_COUNT = "blocked_count"
    private const val KEY_BLOCKED_COUNTS_BY_PACKAGE = "blocked_counts_by_package"
    private const val KEY_BLOCKED_NOTIFICATIONS = "blocked_notifications"
    private const val KEY_SELECTED_PACKAGES = "selected_packages"
    private val DEFAULT_PACKAGES =
        listOf("com.tencent.wemeet.app", "com.google.android.youtube", "com.spotify.music")

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false) && hasNotificationListenerAccess(context)

    fun setEnabled(
        context: Context,
        enabled: Boolean,
    ) {
        prefs(context)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply()
    }

    fun blockedCount(context: Context): Int = blockedNotifications(context).size

    fun incrementBlocked(
        context: Context,
        packageName: String,
    ) {
        if (packageName == context.packageName) return
        val entry =
            BlockedNotification(
                id = UUID.randomUUID().toString(),
                packageName = packageName,
                appName = appNameForPackage(context, packageName),
                title = "",
                text = "",
                postTimeMillis = System.currentTimeMillis(),
                hasLaunchIntent = hasLaunchIntent(context, packageName),
            )
        saveBlockedNotification(context, entry)
    }

    fun blockedCountsByPackage(context: Context): Map<String, Int> =
        blockedNotifications(context).groupingBy { it.packageName }.eachCount()

    fun blockedNotifications(context: Context): List<BlockedNotification> =
        BlockedNotificationStore.decode(prefs(context).getString(KEY_BLOCKED_NOTIFICATIONS, null))
            .filterNot { it.packageName == context.packageName }

    fun recordBlockedNotification(
        context: Context,
        sbn: StatusBarNotification,
    ) {
        if (sbn.packageName == context.packageName) return
        val notification = sbn.notification
        val packageName = sbn.packageName
        val entry =
            BlockedNotification(
                id = UUID.randomUUID().toString(),
                packageName = packageName,
                appName = appNameForPackage(context, packageName),
                title = notification.titleText(),
                text = notification.bodyText(),
                postTimeMillis = sbn.postTime.takeIf { it > 0L } ?: System.currentTimeMillis(),
                hasLaunchIntent = notification.contentIntent != null || hasLaunchIntent(context, packageName),
            )
        saveBlockedNotification(context, entry)
    }

    @Synchronized
    fun clearBlockedNotifications(context: Context): Int {
        val entries = blockedNotifications(context)
        prefs(context)
            .edit()
            .remove(KEY_BLOCKED_NOTIFICATIONS)
            .remove(KEY_BLOCKED_COUNT)
            .remove(KEY_BLOCKED_COUNTS_BY_PACKAGE)
            .apply()
        return entries.size
    }

    fun selectedPackages(context: Context): Set<String> =
        (prefs(context).getStringSet(KEY_SELECTED_PACKAGES, DEFAULT_PACKAGES.toSet()) ?: emptySet())
            .filterNot { it == context.packageName }
            .toSet()

    fun setPackageSelected(
        context: Context,
        packageName: String,
        selected: Boolean,
    ) {
        if (packageName == context.packageName) return
        val current = selectedPackages(context).toMutableSet()
        if (selected) current += packageName else current -= packageName
        prefs(context).edit().putStringSet(KEY_SELECTED_PACKAGES, current).apply()
    }

    fun snapshot(context: Context): NotificationCleanerSnapshot {
        val hasAccess = hasNotificationListenerAccess(context)
        if (!hasAccess) {
            return NotificationCleanerSnapshot(
                hasAccess = false,
                enabled = false,
                blockedCount = 0,
                blockedCountsByPackage = emptyMap(),
                blockedNotifications = emptyList(),
                apps = emptyList(),
                selectedPackages = emptySet(),
            )
        }
        return NotificationCleanerSnapshot(
            hasAccess = true,
            enabled = prefs(context).getBoolean(KEY_ENABLED, false),
            blockedCount = blockedCount(context),
            blockedCountsByPackage = blockedCountsByPackage(context),
            blockedNotifications = blockedNotifications(context),
            apps = apps(context),
            selectedPackages = selectedPackages(context),
        )
    }

    fun apps(context: Context): List<BlockableNotificationApp> {
        val selected = selectedPackages(context)
        return launcherApps(context)
            .sortedWith(
                compareByDescending<BlockableNotificationApp> { it.packageName in DEFAULT_PACKAGES }
                    .thenByDescending { it.packageName in selected }
                    .thenBy { it.appName.lowercase() },
            ).ifEmpty {
            listOf(
                BlockableNotificationApp("Tencent Meeting", "com.tencent.wemeet.app"),
                BlockableNotificationApp("YouTube", "com.google.android.youtube"),
                BlockableNotificationApp("Spotify", "com.spotify.music"),
            )
        }
    }

    fun shouldBlock(
        context: Context,
        packageName: String,
    ): Boolean = packageName != context.packageName && isEnabled(context) && packageName in selectedPackages(context)

    fun hasNotificationListenerAccess(context: Context): Boolean {
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        val component = ComponentName(context, StorageCleanNotificationListener::class.java).flattenToString()
        return flat?.split(':')?.any { it.equals(component, ignoreCase = true) } == true
    }

    @Synchronized
    private fun saveBlockedNotification(
        context: Context,
        entry: BlockedNotification,
    ) {
        val updated = BlockedNotificationStore.normalize(listOf(entry) + blockedNotifications(context))
        prefs(context)
            .edit()
            .putString(KEY_BLOCKED_NOTIFICATIONS, BlockedNotificationStore.encode(updated))
            .remove(KEY_BLOCKED_COUNT)
            .remove(KEY_BLOCKED_COUNTS_BY_PACKAGE)
            .apply()
    }

    private fun appNameForPackage(
        context: Context,
        packageName: String,
    ): String =
        runCatching {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        }.getOrDefault(packageName)

    private fun hasLaunchIntent(
        context: Context,
        packageName: String,
    ): Boolean = context.packageManager.getLaunchIntentForPackage(packageName) != null

    private fun Notification.titleText(): String =
        extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

    private fun Notification.bodyText(): String {
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        if (bigText.isNotBlank()) return bigText
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        if (text.isNotBlank()) return text
        val lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        if (!lines.isNullOrEmpty()) return lines.joinToString("\n") { it.toString() }
        return tickerText?.toString().orEmpty()
    }

    private fun launcherApps(context: Context): List<BlockableNotificationApp> {
        val pm = context.packageManager
        val launcherIntent =
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
        val resolvedApps =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
                )
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
            }
        return resolvedApps
            .asSequence()
            .mapNotNull { it.activityInfo }
            .filter { it.packageName != context.packageName }
            .distinctBy { it.packageName }
            .map { activityInfo ->
                BlockableNotificationApp(
                    appName = activityInfo.loadLabel(pm)?.toString()?.ifBlank { activityInfo.packageName } ?: activityInfo.packageName,
                    packageName = activityInfo.packageName,
                )
            }.toList()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

}

internal object BlockedNotificationStore {
    private const val MAX_BLOCKED_NOTIFICATIONS = 100
    private const val LINE_SEPARATOR = "\n"
    private const val FIELD_SEPARATOR = "|"
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    fun normalize(entries: List<BlockedNotification>): List<BlockedNotification> =
        entries
            .filter { it.id.isNotBlank() && it.packageName.isNotBlank() }
            .sortedByDescending { it.postTimeMillis }
            .distinctBy { it.id }
            .take(MAX_BLOCKED_NOTIFICATIONS)

    fun encode(entries: List<BlockedNotification>): String =
        normalize(entries).joinToString(LINE_SEPARATOR) { entry ->
            listOf(
                encodeField(entry.id),
                encodeField(entry.packageName),
                encodeField(entry.appName),
                encodeField(entry.title),
                encodeField(entry.text),
                entry.postTimeMillis.toString(),
                if (entry.hasLaunchIntent) "1" else "0",
            ).joinToString(FIELD_SEPARATOR)
        }

    fun decode(value: String?): List<BlockedNotification> {
        if (value.isNullOrBlank()) return emptyList()
        return normalize(
            value
                .lineSequence()
                .mapNotNull(::decodeEntry)
                .toList(),
        )
    }

    private fun decodeEntry(line: String): BlockedNotification? =
        runCatching {
            val parts = line.split(FIELD_SEPARATOR)
            if (parts.size != 7) return null
            BlockedNotification(
                id = decodeField(parts[0]),
                packageName = decodeField(parts[1]),
                appName = decodeField(parts[2]),
                title = decodeField(parts[3]),
                text = decodeField(parts[4]),
                postTimeMillis = parts[5].toLong(),
                hasLaunchIntent = parts[6] == "1",
            )
        }.getOrNull()

    private fun encodeField(value: String): String =
        encoder.encodeToString(value.toByteArray(Charsets.UTF_8))

    private fun decodeField(value: String): String =
        String(decoder.decode(value), Charsets.UTF_8)
}

class StorageCleanNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (NotificationDataSource.shouldBlock(this, sbn.packageName)) {
            NotificationDataSource.recordBlockedNotification(this, sbn)
            cancelNotification(sbn.key)
        }
    }
}
