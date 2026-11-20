package com.aicleanerstorage.spaceclean.app.view.home.ui


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

data class ToolNotificationSpec(
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val destination: AppDestination,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val actionRes: Int,
)

val ToolNotificationSpecs: List<ToolNotificationSpec> =
    listOf(
        ToolNotificationSpec(
            titleRes = R.string.device_info,
            descriptionRes = R.string.common_tool_device_desc,
            destination = AppDestination.DeviceInfo,
            iconRes = R.drawable.ic_device_phone,
            actionRes = R.string.view_now,
        ),
        ToolNotificationSpec(
            titleRes = R.string.junk_removal,
            descriptionRes = R.string.notification_tool_junk_desc,
            destination = AppDestination.JunkClean,
            iconRes = R.drawable.junk_cleaning_trash_figma,
            actionRes = R.string.scan_now,
        ),
        ToolNotificationSpec(
            titleRes = R.string.battery_info,
            descriptionRes = R.string.common_tool_battery_desc,
            destination = AppDestination.BatteryInfo,
            iconRes = R.drawable.ic_battery,
            actionRes = R.string.view_now,
        ),
        ToolNotificationSpec(
            titleRes = R.string.network_scan,
            descriptionRes = R.string.notification_tool_network_scan_desc,
            destination = AppDestination.NetworkScan,
            iconRes = R.drawable.ic_network_scan,
            actionRes = R.string.scan_now,
        ),
        ToolNotificationSpec(
            titleRes = R.string.network_usage,
            descriptionRes = R.string.notification_tool_network_usage_desc,
            destination = AppDestination.NetworkUsage,
            iconRes = R.drawable.ic_network_usage,
            actionRes = R.string.view_now,
        ),
        ToolNotificationSpec(
            titleRes = R.string.notification_cleaner,
            descriptionRes = R.string.common_tool_notification_cleaner_desc,
            destination = AppDestination.NotificationCleaner,
            iconRes = R.drawable.ic_notification_cleaner,
            actionRes = R.string.check_now,
        ),
    )

fun toolNotificationSpec(destination: AppDestination): ToolNotificationSpec? =
    ToolNotificationSpecs.firstOrNull { it.destination == destination }
