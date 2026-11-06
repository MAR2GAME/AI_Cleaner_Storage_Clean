package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.app.navigation.owner.FeatureOwner
import com.aicleanerstorage.spaceclean.app.navigation.owner.JunkCleanRouteOwner
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
) {
    composable<AppDestination.JunkClean> { entry ->
        JunkCleanRouteOwner(FeatureOwner.JunkClean(entry), navigator, featureFlow)
    }
}
