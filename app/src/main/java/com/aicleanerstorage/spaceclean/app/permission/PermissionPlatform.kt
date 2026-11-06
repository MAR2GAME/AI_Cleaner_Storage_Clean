package com.aicleanerstorage.spaceclean.app.permission

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

internal fun permissionGranted(context: Context, permission: PermissionType): Boolean = when (permission) {
    PermissionType.StorageFiles -> if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
        else runtimePermissions(permission).all { context.hasPermission(it) }
    PermissionType.Location -> context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    PermissionType.UsageAccess -> (context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager)
        .checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
    PermissionType.NotificationListener -> Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        ?.split(':')?.any { android.content.ComponentName.unflattenFromString(it)?.packageName == context.packageName } == true
    PermissionType.Overlay -> Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context)
    PermissionType.PostNotifications -> Build.VERSION.SDK_INT < 33 || context.hasPermission(Manifest.permission.POST_NOTIFICATIONS)
}

internal fun runtimePermissions(permission: PermissionType): List<String> = when (permission) {
    PermissionType.StorageFiles -> if (Build.VERSION.SDK_INT >= 30) emptyList() else
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    PermissionType.Location -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    PermissionType.PostNotifications -> if (Build.VERSION.SDK_INT >= 33) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList()
    else -> emptyList()
}

internal fun permissionSettings(context: Context, permission: PermissionType): List<Intent> = when (permission) {
    PermissionType.StorageFiles -> if (Build.VERSION.SDK_INT >= 30) listOf(
        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${context.packageName}")),
        Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), appSettingsIntent(context),
    ) else listOf(appSettingsIntent(context))
    PermissionType.UsageAccess -> listOf(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), appSettingsIntent(context))
    PermissionType.NotificationListener -> listOf(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), appSettingsIntent(context))
    PermissionType.Overlay -> if (Build.VERSION.SDK_INT >= 23) listOf(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")), appSettingsIntent(context),
    ) else listOf(appSettingsIntent(context))
    else -> listOf(appSettingsIntent(context))
}

internal fun shouldRequestRuntime(context: Context, permission: PermissionType, preferences: PermissionPreferences): Boolean =
    when (permission) {
        PermissionType.Location -> !preferences.hasRequestedLocationRuntimePermissionBefore() ||
            runtimePermissions(permission).any { context.permissionActivity()?.shouldShowRequestPermissionRationale(it) == true }
        PermissionType.PostNotifications -> !preferences.hasRequestedNotificationRuntimePermissionBefore() ||
            context.postNotificationRationale()
        else -> true
    }

internal fun Context.hasPermission(value: String) = ContextCompat.checkSelfPermission(this, value) == PackageManager.PERMISSION_GRANTED
internal tailrec fun Context.permissionActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.permissionActivity()
    else -> null
}
internal fun Context.postNotificationRationale() = Build.VERSION.SDK_INT >= 33 &&
    permissionActivity()?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true
internal fun appSettingsIntent(context: Context) = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
