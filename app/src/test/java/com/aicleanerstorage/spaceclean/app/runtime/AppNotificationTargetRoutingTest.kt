package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.core.contracts.startup.NotificationLaunchSource
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNotificationTargetRoutingTest {
    @Test
    fun notificationTargetOutsideSplashRoutesThroughSplash() {
        NotificationLaunchSource.entries.forEach { source ->
            assertTrue(
                shouldRouteNotificationRequestToSplash(
                    pendingRequest = notificationRequest(source),
                    currentRoute = AppDestination.Main().route,
                ),
            )
        }
    }

    @Test
    fun notificationTargetAlreadyOnSplashIsConsumedThere() {
        assertFalse(
            shouldRouteNotificationRequestToSplash(
                pendingRequest = notificationRequest(NotificationLaunchSource.InitialIntent),
                currentRoute = AppDestination.Splash.route,
            ),
        )
    }

    @Test
    fun unknownRouteDoesNotConsumeNotificationTarget() {
        assertFalse(
            shouldRouteNotificationRequestToSplash(
                pendingRequest = notificationRequest(NotificationLaunchSource.NewIntent),
                currentRoute = null,
            ),
        )
    }

    @Test
    fun normalOrConsumedRequestDoesNotRouteToSplash() {
        assertFalse(shouldRouteNotificationRequestToSplash(AppLaunchRequest.Normal, AppDestination.Main().route))
        assertFalse(shouldRouteNotificationRequestToSplash(null, AppDestination.Main().route))
    }

    private fun notificationRequest(source: NotificationLaunchSource): AppLaunchRequest.NotificationTarget =
        AppLaunchRequest.NotificationTarget(
            route = AppDestination.NotificationCleaner.route,
            source = source,
        )
}
