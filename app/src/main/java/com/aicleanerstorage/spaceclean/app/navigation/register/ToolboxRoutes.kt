package com.aicleanerstorage.spaceclean.app.navigation.register

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureFlowRuntime
import com.aicleanerstorage.spaceclean.app.runtime.external.ExternalActivityLauncher
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.BatteryInfoRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanDevicesRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerRoute
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerToolboxRoutes(
    navigator: AppNavigator,
    featureFlow: FeatureFlowRuntime,
    externalActivities: ExternalActivityLauncher,
) {
    composable(AppDestination.DeviceInfo.route) {
        DeviceInfoRoute(
            navigator = navigator,
            viewModel = koinViewModel<DeviceInfoViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable(AppDestination.BatteryInfo.route) {
        BatteryInfoRoute(
            navigator = navigator,
            viewModel = koinViewModel<BatteryInfoViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable(AppDestination.AppUsage.route) {
        AppUsageRoute(
            navigator = navigator,
            viewModel = koinViewModel<AppUsageViewModel>(),
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }
    composable(AppDestination.NetworkUsage.route) {
        NetworkUsageRoute(
            navigator = navigator,
            viewModel = koinViewModel<NetworkUsageViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable(AppDestination.NetworkScan.route) {
        NetworkScanRoute(
            navigator = navigator,
            viewModel = koinViewModel<NetworkScanViewModel>(),
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }
    composable(AppDestination.NetworkScanDevices.route) {
        NetworkScanDevicesRoute(navigator, koinViewModel<NetworkScanDevicesViewModel>())
    }
    composable(AppDestination.NetworkSpeed.route) {
        NetworkSpeedRoute(
            navigator = navigator,
            viewModel = koinViewModel<NetworkSpeedViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable(AppDestination.WhatsAppCleaner.route) {
        WhatsAppCleanerRoute(
            navigator = navigator,
            viewModel = koinViewModel<WhatsAppCleanerViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable(AppDestination.NotificationCleaner.route) {
        NotificationCleanerRoute(
            navigator = navigator,
            viewModel = koinViewModel<NotificationCleanerViewModel>(),
            featureFlow = featureFlow,
        )
    }
}
