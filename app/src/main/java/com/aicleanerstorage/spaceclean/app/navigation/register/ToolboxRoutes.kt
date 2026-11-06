package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.ui.AppUsageRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.ui.BatteryInfoRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.ui.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanDevicesRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.ui.NetworkSpeedRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.ui.NetworkUsageRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.ui.WhatsAppCleanerRoute
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.ui.NotificationCleanerRoute
import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerToolboxRoutes(
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    composable<AppDestination.DeviceInfo> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DeviceInfoRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<DeviceInfoViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable<AppDestination.BatteryInfo> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        BatteryInfoRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<BatteryInfoViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable<AppDestination.AppUsage> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        AppUsageRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<AppUsageViewModel>(),
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }
    composable<AppDestination.NetworkUsage> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkUsageRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkUsageViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable<AppDestination.NetworkScan> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkScanRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkScanViewModel>(),
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }
    composable<AppDestination.NetworkScanDevices> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkScanDevicesRoute(pageNavigator, koinViewModel<NetworkScanDevicesViewModel>())
    }
    composable<AppDestination.NetworkSpeed> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkSpeedRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkSpeedViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable<AppDestination.WhatsAppCleaner> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        WhatsAppCleanerRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<WhatsAppCleanerViewModel>(),
            featureFlow = featureFlow,
        )
    }
    composable<AppDestination.NotificationCleaner> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NotificationCleanerRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NotificationCleanerViewModel>(),
            featureFlow = featureFlow,
        )
    }
}
