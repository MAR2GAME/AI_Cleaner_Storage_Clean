package com.aicleanerstorage.spaceclean.common.ads

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination

import com.aicleanerstorage.spaceclean.common.feature.FeatureKey

data class RouteEntryAdDecision(
    val feature: FeatureKey,
    val route: String,
)

class RouteEntryAdPolicy(
    private val routeBlacklist: Set<String> = defaultRouteBlacklist,
    private val sourceBlacklist: Set<String> = defaultSourceBlacklist,
    private val entryWhitelist: Set<String> = defaultEntryWhitelist,
) {
    fun entryAdDecision(
        fromRoute: String?,
        targetRoute: String?,
    ): RouteEntryAdDecision? {
        val normalizedTarget = targetRoute.normalizedRoute() ?: return null
        val normalizedSource = fromRoute.normalizedRoute()

        if (normalizedTarget in routeBlacklist) return null
        if (normalizedSource != null && normalizedSource in sourceBlacklist) return null
        if (normalizedTarget !in entryWhitelist) return null

        val targetFeature = AppDestination.featureForRoute(normalizedTarget) ?: return null
        val sourceFeature = normalizedSource?.let(AppDestination::featureForRoute)
        if (sourceFeature == targetFeature) return null

        return RouteEntryAdDecision(
            feature = targetFeature,
            route = normalizedTarget,
        )
    }

    private fun String?.normalizedRoute(): String? =
        this
            ?.substringBefore("?")
            ?.takeIf(String::isNotBlank)

    companion object {
        private const val ROUTE_HOME_FILE_MANAGER = "home_file_manager"
        private const val ROUTE_HOME_TOOLBOX = "home_toolbox"

        val defaultRouteBlacklist: Set<String> =
            setOf(
                AppDestination.Splash.route,
                AppDestination.OnboardingScan.route,
                AppDestination.Home.route,
                ROUTE_HOME_FILE_MANAGER,
                ROUTE_HOME_TOOLBOX,
                AppDestination.Settings.route,
                AppDestination.ManagePermissions.route,
            )

        val defaultSourceBlacklist: Set<String> =
            setOf(
                AppDestination.Splash.route,
                AppDestination.OnboardingScan.route,
                AppDestination.ManagePermissions.route,
            )

        val defaultEntryWhitelist: Set<String> =
            AppDestination.featureEntries
                .mapTo(linkedSetOf(), AppDestination::route)
    }
}

typealias AdNavigationPolicy = RouteEntryAdPolicy
