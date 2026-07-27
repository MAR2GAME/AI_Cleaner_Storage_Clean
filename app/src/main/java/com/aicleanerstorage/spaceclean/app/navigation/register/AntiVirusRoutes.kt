package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.feature.antivirus.AntiVirusRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.ui.DeepScanVirusScreen
import com.aicleanerstorage.spaceclean.feature.antivirus.NoVirusResultRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.ui.QuickScanVirusScreen
import com.aicleanerstorage.spaceclean.feature.antivirus.ScanVirusResultRoute
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerAntiVirusRoutes(
    navController: NavHostController,
    navigator: AppNavigator,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    composable(AppDestination.AntiVirus.route) {
        val viewModel: VirusScanViewModel = koinViewModel()
        AntiVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
            featureFlow = featureFlow,
            externalActivities = externalActivities,
        )
    }

    composable(AppDestination.VirusQuickScan.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.antiVirusViewModelOwnerOr(backStackEntry)
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        QuickScanVirusScreen(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable(AppDestination.VirusDeepScan.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.antiVirusViewModelOwnerOr(backStackEntry)
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        DeepScanVirusScreen(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable(AppDestination.VirusResult.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.antiVirusViewModelOwnerOr(backStackEntry)
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        ScanVirusResultRoute(
            navigator = navigator,
            viewModel = viewModel,
            externalActivities = externalActivities,
        )
    }

    composable(AppDestination.NoVirusResult.route) {
        NoVirusResultRoute(navigator)
    }
}

private fun NavHostController.antiVirusViewModelOwnerOr(
    fallback: NavBackStackEntry,
): NavBackStackEntry =
    runCatching { getBackStackEntry(AppDestination.AntiVirus.route) }.getOrDefault(fallback)
