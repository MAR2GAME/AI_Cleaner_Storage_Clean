package com.aicleanerstorage.spaceclean.navigation

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.navigation.FeatureEntryNavigator
import com.aicleanerstorage.spaceclean.common.ads.AdNavigationPolicy
import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.ads.once
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

    @Test
    fun `notification feature target waits for entry ad continuation`() {
        val navigator = RecordingNavigator(current = AppDestination.Home.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.openNotificationTarget(AppDestination.NotificationCleaner.route)

        assertEquals(listOf("home->notification_cleaner"), interstitials.routeRequests)
        assertEquals(listOf("enter:NOTIFICATION_CLEANER:notification_cleaner"), interstitials.adRequests)
        assertEquals(emptyList<String>(), navigator.calls)
        assertEquals(emptyList<String>(), navigator.notificationTargets)

        interstitials.continueLast()
        interstitials.continueLast()

        assertEquals(listOf(AppDestination.NotificationCleaner.route), navigator.notificationTargets)
    }

    @Test
    fun `notification home target bypasses entry ad`() {
        val navigator = RecordingNavigator(current = AppDestination.JunkClean.route)
        val interstitials = PolicyAwareInterstitialRunner()
        val router = FeatureEntryNavigator(navigator, interstitials)

        router.openNotificationTarget(AppDestination.HomeToolbox.route)

        assertEquals(emptyList<String>(), interstitials.routeRequests)
        assertEquals(emptyList<String>(), interstitials.adRequests)
        assertEquals(listOf(AppDestination.HomeToolbox.route), navigator.notificationTargets)
    }

    @Test
    fun `notification feature target omits startup route as ad source`() {
        val splashNavigator = RecordingNavigator(current = AppDestination.Splash.route)
        val splashInterstitials = PolicyAwareInterstitialRunner()
        FeatureEntryNavigator(splashNavigator, splashInterstitials)
            .openNotificationTarget(AppDestination.JunkClean.route)

        assertEquals(listOf("->scan"), splashInterstitials.routeRequests)

        val onboardingNavigator = RecordingNavigator(current = AppDestination.OnboardingScan.route)
        val onboardingInterstitials = PolicyAwareInterstitialRunner()
        FeatureEntryNavigator(onboardingNavigator, onboardingInterstitials)
            .openNotificationTarget(AppDestination.JunkClean.route)

        assertEquals(listOf("->scan"), onboardingInterstitials.routeRequests)
    }
}

private class RecordingNavigator(
    private var current: String?,
) : AppNavigator {
    val calls = mutableListOf<String>()
    val notificationTargets = mutableListOf<String>()

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

    override fun openNotificationTarget(route: String) {
        notificationTargets += route
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
            pendingContinuations += once(onContinue)
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
        pendingContinuations.lastOrNull()?.invoke()
    }
}
