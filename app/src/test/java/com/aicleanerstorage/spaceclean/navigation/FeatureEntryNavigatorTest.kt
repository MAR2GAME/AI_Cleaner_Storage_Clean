package com.aicleanerstorage.spaceclean.navigation

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.navigation.FeatureEntryNavigator
import com.aicleanerstorage.spaceclean.common.ads.AdNavigationPolicy
import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import org.junit.Assert.assertEquals
import org.junit.Test

class FeatureEntryNavigatorTest {
    @Test
    fun `resetTo feature entry waits for entry ad continuation`() {
        val navigator = RecordingNavigator(current = AppDestination.Home.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.resetTo(AppDestination.DeviceInfo)

        assertEquals(listOf("home->device_info"), interstitials.routeRequests)
        assertEquals(listOf("enter:DEVICE_INFO:device_info"), interstitials.adRequests)
        assertEquals(emptyList<String>(), navigator.calls)

        interstitials.continueLast()

        assertEquals(listOf("resetTo:device_info"), navigator.calls)
    }

    @Test
    fun `resetTo home and settings continue without entry ad`() {
        val navigator = RecordingNavigator(current = AppDestination.JunkClean.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.resetTo(AppDestination.Home)
        router.resetTo(AppDestination.Settings)

        assertEquals(
            listOf(
                "scan->home",
                "home->settings",
            ),
            interstitials.routeRequests,
        )
        assertEquals(emptyList<String>(), interstitials.adRequests)
        assertEquals(
            listOf(
                "resetTo:home",
                "resetTo:settings",
            ),
            navigator.calls,
        )
    }

    @Test
    fun `resetToRoute recognized feature uses entry policy and preserves final route`() {
        val navigator = RecordingNavigator(current = AppDestination.Home.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.resetToRoute(AppDestination.NetworkScan.route)

        assertEquals(listOf("home->network_scan"), interstitials.routeRequests)
        assertEquals(listOf("enter:NETWORK_SCAN:network_scan"), interstitials.adRequests)
        assertEquals(emptyList<String>(), navigator.calls)

        interstitials.continueLast()

        assertEquals(listOf("resetToRoute:network_scan"), navigator.calls)
    }

    @Test
    fun `resetToRoute unknown route delegates directly`() {
        val navigator = RecordingNavigator(current = AppDestination.Home.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.resetToRoute("unknown_route")

        assertEquals(emptyList<String>(), interstitials.routeRequests)
        assertEquals(emptyList<String>(), interstitials.adRequests)
        assertEquals(listOf("resetToRoute:unknown_route"), navigator.calls)
    }
}

private class RecordingNavigator(
    private var current: String?,
) : AppNavigator {
    val calls = mutableListOf<String>()

    override val currentRoute: String?
        get() = current

    override fun open(destination: AppDestination, args: Map<String, String>) {
        calls += "open:${destination.route}"
        current = destination.route
    }

    override fun openRoute(route: String) {
        calls += "openRoute:$route"
        current = route
    }

    override fun replace(destination: AppDestination) {
        calls += "replace:${destination.route}"
        current = destination.route
    }

    override fun resetTo(destination: AppDestination) {
        calls += "resetTo:${destination.route}"
        current = destination.route
    }

    override fun resetToRoute(route: String) {
        calls += "resetToRoute:$route"
        current = route
    }

    override fun home() {
        calls += "home"
        current = AppDestination.Home.route
    }

    override fun back(): Boolean {
        calls += "back"
        return true
    }
}

private class PolicyAwareInterstitialRunner(
    private val policy: AdNavigationPolicy = AdNavigationPolicy(),
) : InterstitialAdRunner {
    val routeRequests = mutableListOf<String>()
    val adRequests = mutableListOf<String>()
    private val pendingContinuations = mutableListOf<() -> Unit>()

    override fun run(scene: AdScene?, requestId: String, onContinue: () -> Unit) {
        if (scene is AdScene.EnterFeature) {
            adRequests += "enter:${scene.feature.name}:${scene.route}"
            pendingContinuations += onContinue
        } else {
            onContinue()
        }
    }

    override fun runRouteEntry(fromRoute: String?, targetRoute: String?, onContinue: () -> Unit) {
        routeRequests += "${fromRoute.orEmpty()}->${targetRoute.orEmpty()}"
        val decision = policy.entryAdDecision(fromRoute, targetRoute)
        if (decision == null) {
            onContinue()
            return
        }
        run(
            scene = AdScene.EnterFeature(decision.feature, decision.route),
            requestId = "route_enter_${decision.feature.name}_${decision.route}",
            onContinue = onContinue,
        )
    }

    fun continueLast() {
        pendingContinuations.removeAt(pendingContinuations.lastIndex).invoke()
    }
}
