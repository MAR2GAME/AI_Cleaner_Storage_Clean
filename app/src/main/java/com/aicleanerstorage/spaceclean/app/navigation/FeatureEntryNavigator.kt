package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import kotlinx.coroutines.*

class FeatureEntryNavigator(
    private val navigator: AppNavigator,
    private val showAd: suspend (InterstitialRequest) -> Unit,
    private val scope: CoroutineScope,
    private val policy: RouteEntryAdPolicy = RouteEntryAdPolicy(),
) : AppNavigator {
    private val pending = mutableMapOf<String, Job>()
    override val currentRoute get() = navigator.currentRoute
    private fun open(target: String, source: String? = currentRoute, navigate: () -> Unit) {
        val decision = policy.entryAdDecision(source, target)
        if (decision == null) { cancelPending(); navigate(); return }
        val id = "route_enter_${decision.feature.name}_${decision.route}"
        if (pending[id]?.isActive == true) return
        val expectedRoute = currentRoute
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                showAd(InterstitialRequest(AdScene.EnterFeature(decision.feature, decision.route), id))
                ensureActive()
                if (currentRoute == expectedRoute) navigate()
            } finally { if (pending[id] === currentCoroutineContext().job) pending.remove(id) }
        }
        pending[id] = job
        job.start()
    }
    private fun cancelPending() { pending.values.toList().forEach { it.cancel() }; pending.clear() }
    override fun open(destination: AppDestination, args: Map<String, String>) = open(destination.route) { navigator.open(destination, args) }
    override fun openRoute(route: String) = open(route) { navigator.openRoute(route) }
    override fun replace(destination: AppDestination) { cancelPending(); navigator.replace(destination) }
    override fun resetTo(destination: AppDestination) = open(destination.route) { navigator.resetTo(destination) }
    override fun resetToRoute(route: String) = open(route) { navigator.resetToRoute(route) }
    override fun openNotificationTarget(route: String) {
        val source = currentRoute?.takeUnless { it == AppDestination.Splash.route || it == AppDestination.OnboardingScan.route }
        if (route in AppDestination.homeRoutes) { cancelPending(); navigator.openNotificationTarget(route) }
        else open(route, source) { navigator.openNotificationTarget(route) }
    }
    override fun home() { cancelPending(); navigator.home() }
    override fun back(): Boolean { cancelPending(); return navigator.back() }
}
