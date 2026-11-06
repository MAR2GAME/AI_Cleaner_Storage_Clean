package com.aicleanerstorage.spaceclean.app.navigation

import androidx.lifecycle.withResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene
import com.aicleanerstorage.spaceclean.core.contracts.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.MainTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class NavHostAppNavigator(
    private val navController: NavHostController,
    private val showAd: suspend (InterstitialRequest) -> Unit,
    private val scope: CoroutineScope,
    private val policy: RouteEntryAdPolicy = RouteEntryAdPolicy(),
) {
    private val requests = EntryNavigationRequests(scope)

    fun forEntry(source: NavBackStackEntry): AppNavigator = object : AppNavigator {
        override fun navigate(destination: AppDestination) = open(source, destination, reset = false)

        override fun resetTo(destination: AppDestination) = open(source, destination, reset = true)

        override fun replaceCurrent(destination: AppDestination) {
            if (!isCurrent(source)) return
            cancelPending()
            navController.navigate(destination) {
                popUpTo(source.destination.id) { inclusive = true }
                launchSingleTop = true
            }
        }

        override fun navigateTopLevel(tab: MainTab) {
            if (!isCurrent(source)) return
            cancelPending()
            showMain(tab)
        }

        override fun backToMain() {
            if (!isCurrent(source)) return
            cancelPending()
            showMain(null)
        }

        override fun back(): Boolean {
            if (!isCurrent(source)) return false
            cancelPending()
            if (source.destination.hasRoute<AppDestination.Main>() ||
                source.destination.hasRoute<AppDestination.Splash>() ||
                source.destination.hasRoute<AppDestination.OnboardingScan>()) return false
            if (!navController.popBackStack()) showMain(null)
            return true
        }
    }

    private fun isCurrent(source: NavBackStackEntry): Boolean =
        navController.currentBackStackEntry?.id == source.id

    private fun open(source: NavBackStackEntry, destination: AppDestination, reset: Boolean) {
        if (!isCurrent(source)) return
        requests.start(source.id, destination) {
            val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
                if (!isCurrent(source)) requests.cancel()
            }
            navController.addOnDestinationChangedListener(listener)
            try {
                val sourceRoute = source.destination.route?.substringBefore('?')
                    ?.takeUnless { reset && it in AppDestination.startupRoutes }
                policy.entryAdDecision(sourceRoute, destination.route)?.let { decision ->
                    showAd(InterstitialRequest(
                        AdScene.EnterFeature(decision.feature, decision.route),
                        "route_enter_${decision.feature.name}_${decision.route}",
                    ))
                }
                ensureActive()
                source.lifecycle.withResumed {
                    if (isCurrent(source)) {
                        navController.removeOnDestinationChangedListener(listener)
                        if (reset) {
                            navController.navigate(destination as? AppDestination.Main ?: AppDestination.Main()) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        if (destination is AppDestination.Main) {
                            if (!reset) showMain(destination.initialTab)
                        }
                        else navController.navigate(destination) { launchSingleTop = true }
                    }
                }
            } finally {
                navController.removeOnDestinationChangedListener(listener)
            }
        }
    }

    private fun showMain(tab: MainTab?) {
        val main = try { navController.getBackStackEntry<AppDestination.Main>() }
        catch (_: IllegalArgumentException) { null }
        if (main != null) {
            if (tab != null) main.savedStateHandle[MAIN_TAB_REQUEST] = tab.name
            if (navController.currentBackStackEntry?.id != main.id) {
                navController.popBackStack<AppDestination.Main>(inclusive = false)
            }
        } else {
            navController.navigate(AppDestination.Main(tab ?: MainTab.Home)) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    private fun cancelPending() {
        requests.cancel()
    }
}

internal class EntryNavigationRequests(private val scope: CoroutineScope) {
    private var pending: Job? = null
    private var key: Pair<String, AppDestination>? = null

    fun start(sourceId: String, destination: AppDestination, action: suspend CoroutineScope.() -> Unit) {
        val requestedKey = sourceId to destination
        if (key == requestedKey && pending?.isActive == true) return
        cancel()
        key = requestedKey
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                action()
            } finally {
                if (pending === coroutineContext[Job]) {
                    pending = null
                    key = null
                }
            }
        }
        pending = job
        job.start()
    }

    fun cancel() {
        pending?.cancel()
        pending = null
        key = null
    }
}

internal const val MAIN_TAB_REQUEST = "main_tab_request"
