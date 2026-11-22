package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.AntiVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.DeepScanVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.NoVirusResultRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.QuickScanVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.ScanVirusResultRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.effect.VirusEffectHandler
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.virusRoutes(
    navController: NavHostController,
    navigator: AppNavigator,
) {
    composable<AppDestination.AntiVirus> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(navigator, viewModel)
        AntiVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusQuickScan> { backStackEntry ->
        val parentEntry =
            remember(backStackEntry) {
                navController.getBackStackEntry<AppDestination.AntiVirus>()
            }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        QuickScanVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusDeepScan> { backStackEntry ->
        val parentEntry =
            remember(backStackEntry, navController) {
                navController.getBackStackEntry<AppDestination.AntiVirus>()
            }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        DeepScanVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusResult> { backStackEntry ->
        val parentEntry =
            remember(backStackEntry) {
                navController.getBackStackEntry<AppDestination.AntiVirus>()
            }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        ScanVirusResultRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.NoVirusResult> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(navigator, viewModel)
        NoVirusResultRoute(navigator, viewModel)
    }
}
