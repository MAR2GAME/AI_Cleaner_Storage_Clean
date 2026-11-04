package com.aicleanerstorage.spaceclean.navigation

import com.aicleanerstorage.spaceclean.app.navigation.*
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureEntryNavigatorTest {
    @Test fun `duplicate feature entry awaits one ad and navigates once`() = runTest {
        val navigator = RecordingNavigator(AppDestination.Home.route)
        val closed = CompletableDeferred<Unit>()
        val requests = mutableListOf<InterstitialRequest>()
        val router = FeatureEntryNavigator(navigator, { requests += it; closed.await() }, this)
        router.resetTo(AppDestination.DeviceInfo)
        router.resetTo(AppDestination.DeviceInfo)
        runCurrent()
        assertEquals(listOf(InterstitialRequest(AdScene.EnterFeature(FeatureKey.DEVICE_INFO, "device_info"),
            "route_enter_DEVICE_INFO_device_info")), requests)
        assertTrue(navigator.calls.isEmpty())
        closed.complete(Unit)
        runCurrent()
        assertEquals(listOf("resetTo:device_info"), navigator.calls)
    }

    @Test fun `home settings and unknown routes bypass ads and preserve navigation`() = runTest {
        val navigator = RecordingNavigator(AppDestination.JunkClean.route)
        val router = FeatureEntryNavigator(navigator, { fail("Unexpected ad") }, this)
        router.resetTo(AppDestination.Home)
        router.resetTo(AppDestination.Settings)
        router.resetToRoute("unknown_route")
        assertEquals(listOf("resetTo:home", "resetTo:settings", "resetToRoute:unknown_route"), navigator.calls)
    }

    @Test fun `notification feature from startup uses entry ad and keeps final target`() = runTest {
        for (source in listOf(AppDestination.Splash.route, AppDestination.OnboardingScan.route)) {
            val navigator = RecordingNavigator(source)
            val requests = mutableListOf<InterstitialRequest>()
            val router = FeatureEntryNavigator(navigator, { requests += it }, this)
            router.openNotificationTarget(AppDestination.JunkClean.route)
            runCurrent()
            assertEquals(1, requests.size)
            assertEquals(listOf(AppDestination.JunkClean.route), navigator.notificationTargets)
        }
    }

    @Test fun `home notification target bypasses entry and cancels pending navigation`() = runTest {
        val navigator = RecordingNavigator(AppDestination.Home.route)
        val late = CompletableDeferred<Unit>()
        val router = FeatureEntryNavigator(navigator, { withContext(NonCancellable) { late.await() } }, this)
        router.open(AppDestination.NetworkScan)
        runCurrent()
        router.openNotificationTarget(AppDestination.HomeToolbox.route)
        late.complete(Unit)
        runCurrent()
        assertTrue(navigator.calls.isEmpty())
        assertEquals(listOf(AppDestination.HomeToolbox.route), navigator.notificationTargets)
    }

    @Test fun `page disposal cancels entry wait and never navigates`() = runTest {
        val navigator = RecordingNavigator(AppDestination.Home.route)
        val scope = CoroutineScope(coroutineContext + Job())
        val router = FeatureEntryNavigator(navigator, { awaitCancellation() }, scope)
        router.resetToRoute(AppDestination.NetworkScan.route)
        runCurrent()
        scope.cancel()
        runCurrent()
        assertTrue(navigator.calls.isEmpty())
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
