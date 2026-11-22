package com.aicleanerstorage.spaceclean.data.local.notificationcleaner

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.domain.model.notification.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.notification.BlockedNotificationModel
import com.aicleanerstorage.spaceclean.domain.model.notification.PostedNotificationModel
import java.util.Base64
import java.util.UUID

internal class NotificationCleanerDataSource(
    private val preferences: PreferencesDataSource,
) {
    private val keyEnabled = "enabled"
    private val keyBlockedCount = "blocked_count"
    private val keyBlockedCountsByPackage = "blocked_counts_by_package"
    private val keyBlockedNotifications = "blocked_notifications"
    private val keySelectedPackages = "selected_packages"
    private val defaultPackages =
        listOf("com.tencent.wemeet.app", "com.google.android.youtube", "com.spotify.music")

    fun isEnabled(
        context: Context,
        listener: ComponentName?,
    ): Boolean = preferences.getBoolean(keyEnabled, false) && hasNotificationListenerAccess(context, listener)

    fun setEnabled(
        context: Context,
        enabled: Boolean,
    ) {
        preferences.edit {
            putBoolean(keyEnabled, enabled)
        }
    }

    fun blockedNotifications(context: Context): List<BlockedNotificationModel> =
        BlockedNotificationCodec
            .decode(preferences.getString(keyBlockedNotifications, ""))
            .filterNot { it.packageName == context.packageName }

    fun recordBlockedNotification(
        context: Context,
        posted: PostedNotificationModel,
    ) {
        if (posted.packageName == context.packageName) return
        val packageName = posted.packageName
        val entry =
            BlockedNotificationModel(
                id = UUID.randomUUID().toString(),
                packageName = packageName,
                appName = appNameForPackage(context, packageName),
                title = posted.title,
                text = posted.text,
                postTimeMillis = posted.postTimeMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
                hasLaunchIntent = posted.hasContentIntent || hasLaunchIntent(context, packageName),
            )
        saveBlockedNotification(context, entry)
    }

    @Synchronized
    fun clearBlockedNotifications(context: Context): Int {
        val entries = blockedNotifications(context)
        preferences.edit {
            remove(keyBlockedNotifications)
            remove(keyBlockedCount)
            remove(keyBlockedCountsByPackage)
        }
        return entries.size
    }

    @Synchronized
    fun clearStoredContent(context: Context) {
        val entries = blockedNotifications(context)
        if (entries.any { it.title.isNotEmpty() || it.text.isNotEmpty() }) {
            val redacted = entries.map { it.copy(title = "", text = "") }
            preferences.edit { putString(keyBlockedNotifications, BlockedNotificationCodec.encode(redacted)) }
        }
    }

    fun selectedPackages(context: Context): Set<String> =
        preferences
            .getStringSet(keySelectedPackages, defaultPackages.toSet())
            .orEmpty()
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
        preferences.edit { putStringSet(keySelectedPackages, current) }
    }

    fun apps(context: Context): List<BlockableNotificationAppModel> {
        val selected = selectedPackages(context)
        return launcherApps(context)
            .sortedWith(
                compareByDescending<BlockableNotificationAppModel> { it.packageName in defaultPackages }
                    .thenByDescending { it.packageName in selected }
                    .thenBy { it.appName.lowercase() },
            ).ifEmpty {
                listOf(
                    BlockableNotificationAppModel("Tencent Meeting", "com.tencent.wemeet.app"),
                    BlockableNotificationAppModel("YouTube", "com.google.android.youtube"),
                    BlockableNotificationAppModel("Spotify", "com.spotify.music"),
                )
            }
    }

    fun shouldBlock(
        context: Context,
        listener: ComponentName?,
        packageName: String,
    ): Boolean = packageName != context.packageName && isEnabled(context, listener) && packageName in selectedPackages(context)

    fun hasNotificationListenerAccess(
        context: Context,
        listener: ComponentName?,
    ): Boolean {
        if (listener == null) return false
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return flat?.split(':')?.any { ComponentName.unflattenFromString(it) == listener } == true
    }

    @Synchronized
    private fun saveBlockedNotification(
        context: Context,
        entry: BlockedNotificationModel,
    ) {
        val updated = listOf(entry) + blockedNotifications(context)
        preferences.edit {
            putString(keyBlockedNotifications, BlockedNotificationCodec.encode(updated))
            remove(keyBlockedCount)
            remove(keyBlockedCountsByPackage)
        }
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

    private fun launcherApps(context: Context): List<BlockableNotificationAppModel> {
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
                BlockableNotificationAppModel(
                    appName = activityInfo.loadLabel(pm)?.toString()?.ifBlank { activityInfo.packageName } ?: activityInfo.packageName,
                    packageName = activityInfo.packageName,
                )
            }.toList()
    }
}

private object BlockedNotificationCodec {
    private const val MAX_BLOCKED_NOTIFICATIONS = 100
    private const val LINE_SEPARATOR = "\n"
    private const val FIELD_SEPARATOR = "|"
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    private fun normalize(entries: List<BlockedNotificationModel>): List<BlockedNotificationModel> =
        entries
            .filter { it.id.isNotBlank() && it.packageName.isNotBlank() }
            .sortedByDescending { it.postTimeMillis }
            .distinctBy { it.id }
            .take(MAX_BLOCKED_NOTIFICATIONS)

    fun encode(entries: List<BlockedNotificationModel>): String =
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

    fun decode(value: String?): List<BlockedNotificationModel> {
        if (value.isNullOrBlank()) return emptyList()
        return normalize(
            value
                .lineSequence()
                .mapNotNull(::decodeEntry)
                .toList(),
        )
    }

    private fun decodeEntry(line: String): BlockedNotificationModel? =
        runCatching {
            val parts = line.split(FIELD_SEPARATOR)
            if (parts.size != 7) return null
            BlockedNotificationModel(
                id = decodeField(parts[0]),
                packageName = decodeField(parts[1]),
                appName = decodeField(parts[2]),
                title = decodeField(parts[3]),
                text = decodeField(parts[4]),
                postTimeMillis = parts[5].toLong(),
                hasLaunchIntent = parts[6] == "1",
            )
        }.getOrNull()

    private fun encodeField(value: String): String = encoder.encodeToString(value.toByteArray(Charsets.UTF_8))

    private fun decodeField(value: String): String = String(decoder.decode(value), Charsets.UTF_8)
}
