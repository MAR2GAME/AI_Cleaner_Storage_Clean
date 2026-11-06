package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember
import androidx.navigation.compose.navigation
import com.aicleanerstorage.spaceclean.app.navigation.AntiVirusFlow
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.AntiVirusRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.DeepScanVirusRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.NoVirusResultRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.QuickScanVirusRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.ScanVirusResultRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerAntiVirusRoutes(
    navController: NavHostController,
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    navigation<AntiVirusFlow>(startDestination = AppDestination.AntiVirus) {
    composable<AppDestination.AntiVirus> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<AntiVirusFlow>() })
        AntiVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }

    composable<AppDestination.VirusQuickScan> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AntiVirusFlow>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        QuickScanVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusDeepScan> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<AntiVirusFlow>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        DeepScanVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusResult> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AntiVirusFlow>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        ScanVirusResultRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
            externalActivities = externalActivities,
        )
    }

    composable<AppDestination.NoVirusResult> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NoVirusResultRoute(pageNavigator, koinViewModel<VirusScanViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<AntiVirusFlow>() }))
    }
}
}
