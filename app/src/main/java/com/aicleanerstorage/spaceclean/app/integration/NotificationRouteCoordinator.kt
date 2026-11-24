package com.aicleanerstorage.spaceclean.app.integration

import android.content.Intent
import android.os.Bundle
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.mar2sdk.core.notify.NotificationRouteIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NotificationRouteCoordinator {
    private val _pending = MutableStateFlow<NotificationRouteAction?>(null)
    val pending: StateFlow<NotificationRouteAction?> = _pending.asStateFlow()
    private val completedActionIds = LinkedHashSet<String>()

    fun accept(intent: Intent): Boolean {
        val destination = NotificationRouteResolver.resolve(intent.getStringExtra(EXTRA_ROUTE)) ?: return false
        val id = NotificationRouteIntent.ensureActionId(intent) ?: UUID.randomUUID().toString()
        if (_pending.value?.id == id || id in completedActionIds) return false
        _pending.value =
            NotificationRouteAction(
                id = id,
                destination = destination,
                source = intent.getStringExtra(EXTRA_APP_OPEN_FROM).orEmpty(),
                scene = intent.getStringExtra(EXTRA_SCENE).orEmpty(),
                waitForForegroundAd = true,
            )
        return true
    }

    fun consume(id: String) {
        if (_pending.value?.id == id) {
            completedActionIds += id
            _pending.value = null
        }
    }

    fun saveState(): Bundle =
        Bundle().apply {
            _pending.value?.let { action ->
                putString(KEY_PENDING_ID, action.id)
                putString(KEY_PENDING_ROUTE, action.destination.routeKey())
                putString(KEY_PENDING_SOURCE, action.source)
                putString(KEY_PENDING_SCENE, action.scene)
                putBoolean(KEY_PENDING_WAIT_FOR_AD, action.waitForForegroundAd)
            }
            putStringArrayList(KEY_COMPLETED_IDS, ArrayList(completedActionIds))
        }

    fun restoreState(savedState: Bundle?) {
        if (savedState == null) return
        completedActionIds.clear()
        completedActionIds += savedState.getStringArrayList(KEY_COMPLETED_IDS).orEmpty()
        val id = savedState.getString(KEY_PENDING_ID) ?: return
        val destination = NotificationRouteResolver.resolveKey(savedState.getString(KEY_PENDING_ROUTE)) ?: return
        _pending.value =
            NotificationRouteAction(
                id = id,
                destination = destination,
                source = savedState.getString(KEY_PENDING_SOURCE).orEmpty(),
                scene = savedState.getString(KEY_PENDING_SCENE).orEmpty(),
                waitForForegroundAd = savedState.getBoolean(KEY_PENDING_WAIT_FOR_AD, true),
            )
    }

    private companion object {
        const val KEY_PENDING_ID = "notification_pending_id"
        const val KEY_PENDING_ROUTE = "notification_pending_route"
        const val KEY_PENDING_SOURCE = "notification_pending_source"
        const val KEY_PENDING_SCENE = "notification_pending_scene"
        const val KEY_PENDING_WAIT_FOR_AD = "notification_pending_wait_for_ad"
        const val KEY_COMPLETED_IDS = "notification_completed_ids"
    }
}

private object NotificationRouteResolver {
    fun resolve(route: String?): AppDestination? =
        when (route?.trim()) {
            "Photos", "Action1" -> AppDestination.PhotosManager
            "Files", "Action2" -> AppDestination.DocumentsManager
            "Videos", "Action3" -> AppDestination.VideosManager
            "Recovered", "Action4" -> AppDestination.PhotosManager
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

    fun resolveKey(route: String?): AppDestination? = resolve(route)
}

private fun AppDestination.routeKey(): String =
    when (this) {
        AppDestination.PhotosManager -> "Photos"
        AppDestination.DocumentsManager -> "Files"
        AppDestination.VideosManager -> "Videos"
        AppDestination.JunkClean -> "/junkClean"
        AppDestination.AntiVirus -> "/virusAnti"
        AppDestination.AppLock -> "/appLock"
        AppDestination.SimilarPhotosManager -> "/manageSimilarPhotos"
        AppDestination.AppUsage -> "/appUsage"
        AppDestination.BatteryInfo -> "/checkBatteryInfo"
        AppDestination.NotificationCleaner -> "/notificationClean"
        AppDestination.WhatsAppCleaner -> "/whatsapp"
        AppDestination.NetworkUsage -> "/networkUsage"
        AppDestination.NetworkScan -> "/networkScan"
        AppDestination.NetworkSpeed -> "/networkSpeed"
        else -> javaClass.simpleName
    }

private const val EXTRA_ROUTE = "Route"
private const val EXTRA_APP_OPEN_FROM = "AppOpenFrom"
private const val EXTRA_SCENE = "Scene"
