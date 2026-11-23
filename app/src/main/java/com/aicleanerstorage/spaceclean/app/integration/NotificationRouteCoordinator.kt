package com.aicleanerstorage.spaceclean.app.integration

import android.content.Intent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.mar2sdk.core.notify.NotificationRouteIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NotificationRouteCoordinator {
    private val _pending = MutableStateFlow<NotificationRouteAction?>(null)
    val pending: StateFlow<NotificationRouteAction?> = _pending.asStateFlow()

    fun accept(intent: Intent, warmDelivery: Boolean): Boolean {
        val destination = NotificationRouteResolver.resolve(intent.getStringExtra(EXTRA_ROUTE)) ?: return false
        val id = NotificationRouteIntent.ensureActionId(intent) ?: UUID.randomUUID().toString()
        if (_pending.value?.id == id) return false
        _pending.value = NotificationRouteAction(
            id = id,
            destination = destination,
            source = intent.getStringExtra(EXTRA_APP_OPEN_FROM).orEmpty(),
            scene = intent.getStringExtra(EXTRA_SCENE).orEmpty(),
            waitForForegroundAd = warmDelivery,
        )
        return true
    }

    fun consume(id: String) {
        if (_pending.value?.id == id) _pending.value = null
    }
}

private object NotificationRouteResolver {
    fun resolve(route: String?): AppDestination? = when (route?.trim()) {
        "/junkClean" -> AppDestination.JunkClean
        "/virusAnti" -> AppDestination.AntiVirus
        "/appLock" -> AppDestination.AppLock
        "/managePhotos" -> AppDestination.PhotosManager
        "/manageSimilarPhotos" -> AppDestination.SimilarPhotosManager
        "/manageVideos" -> AppDestination.VideosManager
        "/appUsage" -> AppDestination.AppUsage
        "/checkBatteryInfo" -> AppDestination.BatteryInfo
        "/notificationClean" -> AppDestination.NotificationCleaner
        "/whatsapp" -> AppDestination.WhatsAppCleaner
        "/networkUsage" -> AppDestination.NetworkUsage
        "/networkScan" -> AppDestination.NetworkScan
        "/networkSpeed" -> AppDestination.NetworkSpeed
        else -> null
    }
}

private const val EXTRA_ROUTE = "Route"
private const val EXTRA_APP_OPEN_FROM = "AppOpenFrom"
private const val EXTRA_SCENE = "Scene"
