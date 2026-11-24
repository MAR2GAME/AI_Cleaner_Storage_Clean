package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.mar2sdk.core.Core

/** 将原项目已有业务埋点迁移到 Mar2SDK。 */
object Mar2Analytics {
    fun trackSplashDisplayed(durationMillis: Long) {
        log(SPLASH_DISPLAY, mapOf(DURATION_TIME to durationMillis.coerceAtLeast(0L)))
    }

    fun trackGuideScanningEntered() {
        log(ENTER_GUIDE_PAGE_SCANNING)
    }

    fun trackGuideScanResultEntered() {
        log(ENTER_GUIDE_PAGE_SCAN_RESULT)
    }

    fun trackGuideContinueClicked() {
        log(CLICK_CONTINUE_GUIDEPAGE)
    }

    fun trackGuideSkipClicked() {
        log(CLICK_SKIP_GUIDEPAGE)
    }

    fun trackHomeEntered(referrerName: String?) {
        log(ENTER_HOMEPAGE, mapOf(REFERRER_NAME to referrerName))
    }

    fun trackCoreFeatureClicked(destination: AppDestination) {
        featureDetails(destination)?.let { details ->
            log(CLICK_CORE_FEATURES_BTN, mapOf(DETAILS to details))
        }
    }

    fun trackCoreFeatureEntered(route: String) {
        featureDetails(route)?.let { details ->
            log(ENTER_CORE_FEATURES_PAGE, mapOf(DETAILS to details))
        }
    }

    fun trackTermsOpened(referrerName: String) {
        log(CHECK_TERMS, mapOf(REFERRER_NAME to referrerName))
    }

    fun trackPrivacyOpened(referrerName: String) {
        log(CHECK_PRIVACY, mapOf(REFERRER_NAME to referrerName))
    }

    fun trackRatePopup(ifOk: Boolean) {
        log(RATE_POPUP, mapOf(IF_OK to ifOk))
    }

    fun trackFileManagerPopup(ifOk: Boolean) {
        log(FILEMANAGER_POPUP, mapOf(IF_OK to ifOk))
    }

    fun trackNotificationPopup(noticeFlag: Int, ifOk: Boolean) {
        log(
            NOTIFICATION_POPUP,
            mapOf(
                NOTICE_FLAG to noticeFlag,
                IF_OK to ifOk,
            ),
        )
    }

    fun trackFilePermissionResult(accepted: Boolean) {
        log(if (accepted) AGREE_FILEMANAGE_PERMISSION else DISAGREE_FILEMANAGE_PERMISSION)
    }

    fun trackNotificationPermissionResult(accepted: Boolean) {
        log(if (accepted) AGREE_PUSH_PERMISSION else DISAGREE_PUSH_PERMISSION)
    }

    fun trackRouteEntered(route: String, previousRoute: String?) {
        if (route.isMainRoute()) {
            trackHomeEntered(homeReferrer(previousRoute))
            return
        }
        trackCoreFeatureEntered(route)
    }

    private fun homeReferrer(previousRoute: String?): String? =
        when {
            previousRoute?.contains("AppDestination.OnboardingScan") == true -> GUIDE_PAGE
            featureDetails(previousRoute) != null -> CORE_FEATURES
            else -> null
        }

    private fun featureDetails(destination: AppDestination): String? =
        when (destination) {
            AppDestination.JunkClean -> JUNK_CLEAN
            AppDestination.AntiVirus -> VIRUS_ANTI
            AppDestination.AppLock -> APP_LOCK
            AppDestination.DeviceInfo -> DEVICE_INFO
            AppDestination.BatteryInfo -> BATTERY_INFO
            AppDestination.AppUsage -> APP_USAGE
            AppDestination.NotificationCleaner -> NOTIFICATION_CLEAN
            AppDestination.WhatsAppCleaner -> WHATSAPP_CLEAN
            AppDestination.NetworkUsage -> NETWORK_USAGE
            AppDestination.NetworkScan -> NETWORK_SCAN
            AppDestination.NetworkSpeed -> NETWORK_SPEED
            AppDestination.PhotosManager,
            AppDestination.SimilarPhotosManager,
            AppDestination.PhotoPrivacyManager,
            AppDestination.ScreenshotsManager,
            AppDestination.VideosManager,
            AppDestination.AudiosManager,
            AppDestination.LargeFilesManager,
            AppDestination.DuplicateFilesManager,
            AppDestination.DocumentsManager,
            -> FILE_MANAGE
            else -> null
        }

    private fun featureDetails(route: String?): String? {
        val value = route ?: return null
        return when {
            value.contains("AppDestination.JunkClean") -> JUNK_CLEAN
            value.contains("AppDestination.AntiVirus") -> VIRUS_ANTI
            value.contains("AppDestination.AppLock") -> APP_LOCK
            value.contains("AppDestination.DeviceInfo") -> DEVICE_INFO
            value.contains("AppDestination.BatteryInfo") -> BATTERY_INFO
            value.contains("AppDestination.AppUsage") -> APP_USAGE
            value.contains("AppDestination.NotificationCleaner") -> NOTIFICATION_CLEAN
            value.contains("AppDestination.WhatsAppCleaner") -> WHATSAPP_CLEAN
            value.contains("AppDestination.NetworkUsage") -> NETWORK_USAGE
            value.contains("AppDestination.NetworkScan") -> NETWORK_SCAN
            value.contains("AppDestination.NetworkSpeed") -> NETWORK_SPEED
            value.contains("AppDestination.PhotosManager") ||
                value.contains("AppDestination.SimilarPhotosManager") ||
                value.contains("AppDestination.PhotoPrivacyManager") ||
                value.contains("AppDestination.ScreenshotsManager") ||
                value.contains("AppDestination.VideosManager") ||
                value.contains("AppDestination.AudiosManager") ||
                value.contains("AppDestination.LargeFilesManager") ||
                value.contains("AppDestination.DuplicateFilesManager") ||
                value.contains("AppDestination.DocumentsManager") -> FILE_MANAGE
            else -> null
        }
    }

    private fun String.isMainRoute(): Boolean = contains("AppDestination.Main")

    private fun log(
        eventName: String,
        params: Map<String, Any?> = emptyMap(),
    ) {
        val cleanParams: Map<String, Any> =
            params.mapNotNull { (key, value) ->
                if (key.isBlank() || value == null) return@mapNotNull null
                key to value
            }.toMap()
        runCatching { Core.log(eventName, cleanParams) }
    }

    private const val SPLASH_DISPLAY = "splash_display"
    private const val ENTER_GUIDE_PAGE_SCANNING = "enter_guide_page_scanning"
    private const val ENTER_GUIDE_PAGE_SCAN_RESULT = "enter_guide_page_scan_result"
    private const val CLICK_CONTINUE_GUIDEPAGE = "click_continue_guidepage"
    private const val CLICK_SKIP_GUIDEPAGE = "click_skip_guidepage"
    private const val AGREE_FILEMANAGE_PERMISSION = "agree_filemanage_permission"
    private const val AGREE_PUSH_PERMISSION = "agree_push_permission"
    private const val DISAGREE_FILEMANAGE_PERMISSION = "disagree_filemanage_permission"
    private const val DISAGREE_PUSH_PERMISSION = "disagree_push_permission"
    private const val ENTER_HOMEPAGE = "enter_homepage"
    private const val CLICK_CORE_FEATURES_BTN = "click_corefeatures_btn"
    private const val ENTER_CORE_FEATURES_PAGE = "enter_corefeatures_page"
    private const val RATE_POPUP = "rate_popup"
    private const val FILEMANAGER_POPUP = "filemanager_popup"
    private const val NOTIFICATION_POPUP = "notification_popup"
    private const val CHECK_PRIVACY = "check_privacy"
    private const val CHECK_TERMS = "check_terms"

    private const val DURATION_TIME = "duration_time"
    private const val REFERRER_NAME = "referrer_name"
    private const val DETAILS = "details"
    private const val IF_OK = "if_ok"
    private const val NOTICE_FLAG = "noticeflag"
    private const val GUIDE_PAGE = "guidepage"
    private const val CORE_FEATURES = "corefeatures"

    private const val JUNK_CLEAN = "junkClean"
    private const val VIRUS_ANTI = "virusAnti"
    private const val APP_LOCK = "appLock"
    private const val FILE_MANAGE = "fileManage"
    private const val DEVICE_INFO = "deviceInfo"
    private const val BATTERY_INFO = "batteryInfo"
    private const val APP_USAGE = "appUsage"
    private const val NOTIFICATION_CLEAN = "notificationClean"
    private const val WHATSAPP_CLEAN = "whatsappClean"
    private const val NETWORK_USAGE = "networkUsage"
    private const val NETWORK_SCAN = "networkScan"
    private const val NETWORK_SPEED = "networkSpeed"
}
