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
        AnalyticsDetails.forDestination(destination)?.let { details ->
            log(CLICK_CORE_FEATURES_BTN, mapOf(DETAILS to details))
        }
    }

    fun trackCoreFeatureEntered(route: String) {
        AnalyticsDetails.forRoute(route)?.let { details ->
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

    fun trackNotificationPopup(
        noticeFlag: Int,
        ifOk: Boolean,
    ) {
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

    fun trackRouteEntered(
        route: String,
        previousRoute: String?,
    ) {
        if (route.isMainRoute()) {
            trackHomeEntered(homeReferrer(previousRoute))
            return
        }
        trackCoreFeatureEntered(route)
    }

    private fun homeReferrer(previousRoute: String?): String? =
        when {
            previousRoute?.contains("AppDestination.OnboardingScan") == true -> GUIDE_PAGE
            AnalyticsDetails.forRoute(previousRoute) != null -> CORE_FEATURES
            else -> null
        }

    private fun String.isMainRoute(): Boolean = contains("AppDestination.Main")

    private fun log(
        eventName: String,
        params: Map<String, Any?> = emptyMap(),
    ) {
        val cleanParams: Map<String, Any> =
            params
                .mapNotNull { (key, value) ->
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

    /** 合规链接来源：启动页。 */
    const val LAUNCH_PAGE = "launchpage"

    /** 合规链接来源：设置页。 */
    const val ABOUT = "about"

    private const val GUIDE_PAGE = "guidepage"
    private const val CORE_FEATURES = "corefeatures"
}
