package com.aicleanerstorage.spaceclean.base.navigation

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

internal const val MAIN_TAB_REQUEST = "main_tab_request"

/** 直接包 [NavHostController] 的导航实现；页面只通过 [AppNavigator] 跳转。 */
class AppNavigatorImpl(
    private val navController: NavHostController,
) : AppNavigator {
    override fun navigate(destination: AppDestination) {
        navController.navigate(destination) { launchSingleTop = true }
    }

    override fun replace(destination: AppDestination) {
        val current = navController.currentBackStackEntry ?: return
        if (current.destination.hasRoute<AppDestination.Main>()) return
        navController.navigate(destination) {
            popUpTo(current.destination.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun resetToRoot(destination: AppDestination) {
        val hasMain = runCatching { navController.getBackStackEntry<AppDestination.Main>() }.isSuccess
        if (hasMain) {
            if (destination is AppDestination.Main) {
                navController
                    .getBackStackEntry<AppDestination.Main>()
                    .savedStateHandle[MAIN_TAB_REQUEST] = destination.initialTab
                navController.popBackStack<AppDestination.Main>(inclusive = false)
            } else {
                navController.navigate(destination) {
                    popUpTo<AppDestination.Main> { inclusive = false }
                    launchSingleTop = true
                }
            }
            return
        }
        if (destination is AppDestination.Main) {
            navController.navigate(destination) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(AppDestination.Main()) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            navController.navigate(destination) { launchSingleTop = true }
        }
    }

    override fun back(): Boolean {
        val current = navController.currentDestination ?: return false
        if (current.hasRoute<AppDestination.Main>()) return false
        return navController.popBackStack()
    }
}
