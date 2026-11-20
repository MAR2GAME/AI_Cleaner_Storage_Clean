package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

internal object AppRouteTransitions {
    fun enterTransition(
        scope: AnimatedContentTransitionScope<NavBackStackEntry>,
        initialDestination: NavDestination?,
        targetDestination: NavDestination?,
    ): EnterTransition {
        if (initialDestination == null || targetDestination.isSplash()) return EnterTransition.None
        if (initialDestination.isSplash()) return fadeIn(animationSpec = fadeSpec())
        return scope.slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = slideSpec(),
        ) + fadeIn(animationSpec = fadeSpec())
    }

    fun exitTransition(
        scope: AnimatedContentTransitionScope<NavBackStackEntry>,
        initialDestination: NavDestination?,
        targetDestination: NavDestination?,
    ): ExitTransition {
        if (initialDestination == null || initialDestination.isSplash() || targetDestination.isSplash()) {
            return ExitTransition.None
        }
        return scope.slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = slideSpec(),
        ) + fadeOut(animationSpec = fadeSpec())
    }

    fun popEnterTransition(
        scope: AnimatedContentTransitionScope<NavBackStackEntry>,
        initialDestination: NavDestination?,
        targetDestination: NavDestination?,
    ): EnterTransition {
        if (initialDestination == null || targetDestination.isSplash()) return EnterTransition.None
        if (initialDestination.isSplash()) return fadeIn(animationSpec = fadeSpec())
        return scope.slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = slideSpec(),
        ) + fadeIn(animationSpec = fadeSpec())
    }

    fun popExitTransition(
        scope: AnimatedContentTransitionScope<NavBackStackEntry>,
        initialDestination: NavDestination?,
        targetDestination: NavDestination?,
    ): ExitTransition {
        if (initialDestination == null || initialDestination.isSplash() || targetDestination.isSplash()) {
            return ExitTransition.None
        }
        return scope.slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = slideSpec(),
        ) + fadeOut(animationSpec = fadeSpec())
    }

    private fun NavDestination?.isSplash(): Boolean = this?.hasRoute<AppDestination.Splash>() == true

    private fun slideSpec() =
        tween<IntOffset>(
            durationMillis = TRANSITION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        )

    private fun fadeSpec() =
        tween<Float>(
            durationMillis = TRANSITION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        )
}

private const val TRANSITION_DURATION_MILLIS = 280
