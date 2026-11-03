package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner

class FeatureEntryNavigator(
    private val navigator: AppNavigator,
    private val interstitialAds: InterstitialAdRunner,
) : AppNavigator {
    override val currentRoute: String?
        get() = navigator.currentRoute

    override fun open(destination: AppDestination, args: Map<String, String>) {
        interstitialAds.runRouteEntry(currentRoute, destination.route) { navigator.open(destination, args) }
    }

    override fun openRoute(route: String) {
        interstitialAds.runRouteEntry(currentRoute, route) { navigator.openRoute(route) }
    }

    override fun replace(destination: AppDestination) = navigator.replace(destination)
    override fun resetTo(destination: AppDestination) {
        interstitialAds.runRouteEntry(currentRoute, destination.route) { navigator.resetTo(destination) }
    }

    override fun resetToRoute(route: String) {
        val destination = AppDestination.forRoute(route)
        if (destination == null) {
            navigator.resetToRoute(route)
            return
        }
        interstitialAds.runRouteEntry(currentRoute, destination.route) { navigator.resetToRoute(route) }
    }

    override fun openNotificationTarget(route: String) {
        val fromRoute =
            navigator.currentRoute?.takeUnless {
                it == AppDestination.Splash.route || it == AppDestination.OnboardingScan.route
            }
        val openTarget = { navigator.openNotificationTarget(route) }
        if (route in AppDestination.homeRoutes) {
            openTarget()
            return
        }
        interstitialAds.runRouteEntry(
            fromRoute = fromRoute,
            targetRoute = route,
            onContinue = openTarget,
        )
    }
    override fun home() = navigator.home()
    override fun back(): Boolean = navigator.back()
}
