package com.aicleanerstorage.spaceclean.app.navigation

import androidx.navigation.NavBackStackEntry
import androidx.lifecycle.withResumed
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.MainTab
import kotlinx.coroutines.CoroutineScope

class NavHostAppNavigator(
    private val navController: NavHostController,
    scope: CoroutineScope,
) {
    private val coordinator = NavigationCoordinator(scope)

    internal val events get() = coordinator.events
    private var committing = false

    internal fun sourceChanged(entryId: String?) {
        if (!committing) coordinator.sourceChanged(entryId)
    }

    internal fun close() = coordinator.cancel()

    fun forEntry(source: NavBackStackEntry): AppNavigator = object : AppNavigator {
        override fun navigate(destination: AppDestination) = submit(source, destination, NavigationKind.Navigate)

        override fun resetTo(destination: AppDestination) = submit(source, destination, NavigationKind.ResetTo)

        override fun replaceCurrent(destination: AppDestination) {
            if (!isCurrent(source)) return
            submit(source, destination, NavigationKind.ReplaceCurrent)
        }

        override fun navigateTopLevel(tab: MainTab) {
            if (!isCurrent(source)) return
            submit(source, AppDestination.Main(tab), NavigationKind.TopLevel)
        }

        override fun backToMain() {
            if (!isCurrent(source)) return
            coordinator.cancelForSource(source.id)
            showMain(null)
        }

        override fun back(): Boolean {
            if (!isCurrent(source)) return false
            coordinator.cancelForSource(source.id)
            if (source.destination.hasRoute<AppDestination.Main>() ||
                source.destination.hasRoute<AppDestination.Splash>() ||
                source.destination.hasRoute<AppDestination.OnboardingScan>()) return false
            if (!navController.popBackStack()) showMain(null)
            return true
        }
    }

    private fun isCurrent(source: NavBackStackEntry): Boolean =
        navController.currentBackStackEntry?.id == source.id

    private fun submit(source: NavBackStackEntry, destination: AppDestination, kind: NavigationKind) {
        if (!isCurrent(source)) return
        coordinator.submit(source.id, destination, kind, source.destination.route?.substringBefore('?')) {
            if (!isCurrent(source)) {
                false
            } else {
                var accepted = false
                source.lifecycle.withResumed {
                    if (isCurrent(source)) {
                        committing = true
                        try {
                            when (kind) {
                                NavigationKind.Navigate -> {
                                    if (destination is AppDestination.Main) showMain(destination.initialTab)
                                    else navController.navigate(destination) { launchSingleTop = true }
                                }
                                NavigationKind.ReplaceCurrent -> navController.navigate(destination) {
                                    popUpTo(source.destination.id) { inclusive = true }
                                    launchSingleTop = true
                                }
                                NavigationKind.ResetTo -> {
                                    navController.navigate(destination as? AppDestination.Main ?: AppDestination.Main()) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    if (destination !is AppDestination.Main) {
                                        navController.navigate(destination) { launchSingleTop = true }
                                    }
                                }
                                NavigationKind.TopLevel -> showMain((destination as AppDestination.Main).initialTab)
                            }
                        } finally {
                            committing = false
                        }
                        accepted = true
                    }
                }
                accepted
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

}

internal const val MAIN_TAB_REQUEST = "main_tab_request"
