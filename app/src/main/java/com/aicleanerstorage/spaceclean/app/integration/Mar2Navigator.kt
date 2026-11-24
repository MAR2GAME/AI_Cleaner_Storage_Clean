package com.aicleanerstorage.spaceclean.app.integration

import android.app.Activity
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.NavControllerAppNavigator
import com.mar2sdk.core.ad.status.AdFormat
import com.mar2sdk.core.ad.policy.ScreenAdTrigger

class Mar2Navigator(
    private val navController: NavHostController,
    private val activity: Activity?,
) : AppNavigator {
    private val delegate = NavControllerAppNavigator(navController)

    override fun navigate(destination: AppDestination) {
        navigateWithAd(destination, Mar2AdPlacementRegistry.enterArea(destination)) {
            delegate.navigate(destination)
        }
    }

    override fun replace(destination: AppDestination) = delegate.replace(destination)

    override fun resetToRoot(destination: AppDestination) {
        if (destination is AppDestination.Main) {
            val source = currentFeature()
            val area = source?.let(Mar2AdPlacementRegistry::returnArea)
            if (source != null && area != null && activity != null) {
                Mar2AdGateway.showInterstitial(
                    activity = activity,
                    areaKey = area,
                    fromRoute = source.javaClass.simpleName,
                    toRoute = "main",
                    onContinue = { delegate.resetToRoot(destination) },
                )
                return
            }
        }
        navigateWithAd(destination, Mar2AdPlacementRegistry.enterArea(destination)) {
            delegate.resetToRoot(destination)
        }
    }

    fun resetToRootForNotification(destination: AppDestination) {
        delegate.resetToRoot(destination)
    }

    override fun back(): Boolean {
        val source = currentFeature() ?: return delegate.back()
        val area = Mar2AdPlacementRegistry.returnArea(source) ?: return delegate.back()
        val current = navController.currentBackStackEntry ?: return false
        if (activity == null) return delegate.back()
        Mar2AdGateway.show(
            activity = activity,
            areaKey = area,
            format = AdFormat.INTER,
            trigger = ScreenAdTrigger.RETURN,
            fromRoute = current.destination.route.orEmpty(),
            toRoute = "main",
            onContinue = { delegate.back() },
        )
        return true
    }

    private fun navigateWithAd(destination: AppDestination, area: String?, onContinue: () -> Unit) {
        val host = activity
        if (area == null || host == null) onContinue() else Mar2AdGateway.showInterstitial(
            activity = host,
            areaKey = area,
            fromRoute = "main",
            toRoute = destination.javaClass.simpleName,
            onContinue = onContinue,
        )
    }

    private fun currentFeature(): AppDestination? = with(navController) {
        when {
            currentDestination?.hasRoute<AppDestination.JunkClean>() == true -> AppDestination.JunkClean
            currentDestination?.hasRoute<AppDestination.AntiVirus>() == true -> AppDestination.AntiVirus
            currentDestination?.hasRoute<AppDestination.VirusQuickScan>() == true -> AppDestination.AntiVirus
            currentDestination?.hasRoute<AppDestination.VirusDeepScan>() == true -> AppDestination.AntiVirus
            currentDestination?.hasRoute<AppDestination.VirusResult>() == true -> AppDestination.AntiVirus
            currentDestination?.hasRoute<AppDestination.NoVirusResult>() == true -> AppDestination.AntiVirus
            currentDestination?.hasRoute<AppDestination.AppLock>() == true -> AppDestination.AppLock
            currentDestination?.hasRoute<AppDestination.DeviceInfo>() == true -> AppDestination.DeviceInfo
            currentDestination?.hasRoute<AppDestination.BatteryInfo>() == true -> AppDestination.BatteryInfo
            currentDestination?.hasRoute<AppDestination.AppUsage>() == true -> AppDestination.AppUsage
            currentDestination?.hasRoute<AppDestination.NetworkUsage>() == true -> AppDestination.NetworkUsage
            currentDestination?.hasRoute<AppDestination.NetworkScan>() == true -> AppDestination.NetworkScan
            currentDestination?.hasRoute<AppDestination.NetworkSpeed>() == true -> AppDestination.NetworkSpeed
            currentDestination?.hasRoute<AppDestination.NotificationCleaner>() == true -> AppDestination.NotificationCleaner
            currentDestination?.hasRoute<AppDestination.WhatsAppCleaner>() == true -> AppDestination.WhatsAppCleaner
            currentDestination?.hasRoute<AppDestination.PhotosManager>() == true -> AppDestination.PhotosManager
            currentDestination?.hasRoute<AppDestination.SimilarPhotosManager>() == true -> AppDestination.SimilarPhotosManager
            currentDestination?.hasRoute<AppDestination.PhotoPrivacyManager>() == true -> AppDestination.PhotoPrivacyManager
            currentDestination?.hasRoute<AppDestination.ScreenshotsManager>() == true -> AppDestination.ScreenshotsManager
            currentDestination?.hasRoute<AppDestination.VideosManager>() == true -> AppDestination.VideosManager
            currentDestination?.hasRoute<AppDestination.AudiosManager>() == true -> AppDestination.AudiosManager
            currentDestination?.hasRoute<AppDestination.LargeFilesManager>() == true -> AppDestination.LargeFilesManager
            currentDestination?.hasRoute<AppDestination.DuplicateFilesManager>() == true -> AppDestination.DuplicateFilesManager
            currentDestination?.hasRoute<AppDestination.DocumentsManager>() == true -> AppDestination.DocumentsManager
            else -> null
        }
    }
}
