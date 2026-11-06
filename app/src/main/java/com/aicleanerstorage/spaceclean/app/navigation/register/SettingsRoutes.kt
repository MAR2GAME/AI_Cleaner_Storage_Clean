package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.ui.ManagePermissionsRoute
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui.SettingsRoute
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerSettingsRoutes(
    navigator: NavHostAppNavigator,
    externalActivities: ExternalActivityTracker,
    analyticsReporter: AnalyticsReporter,
) {
    composable<AppDestination.Settings> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        SettingsRoute(
            viewModel = koinViewModel(),
            termsUrl = com.aicleanerstorage.spaceclean.app.AppConfig.TERMS_OF_SERVICE_URL,
            privacyUrl = com.aicleanerstorage.spaceclean.app.AppConfig.PRIVACY_POLICY_URL,
            navigator = pageNavigator,
            externalActivities = externalActivities,
            analyticsReporter = analyticsReporter,
        )
    }
    composable<AppDestination.ManagePermissions> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        ManagePermissionsRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<ManagePermissionsViewModel>(),
        )
    }
}
