package com.aicleanerstorage.spaceclean.common.analytics

import com.aicleanerstorage.spaceclean.common.feature.FeatureKey

object AnalyticsPayloadMapper {
    fun map(event: AnalyticsEvent): AnalyticsPayload =
        when (event) {
            AnalyticsEvent.GuideScanningEntered -> AnalyticsPayload(AnalyticsTracker.Event.ENTER_GUIDE_PAGE_SCANNING)
            AnalyticsEvent.GuideScanResultEntered -> AnalyticsPayload(AnalyticsTracker.Event.ENTER_GUIDE_PAGE_SCAN_RESULT)
            AnalyticsEvent.GuideContinueClicked -> AnalyticsPayload(AnalyticsTracker.Event.CLICK_CONTINUE_GUIDEPAGE)
            AnalyticsEvent.GuideSkipClicked -> AnalyticsPayload(AnalyticsTracker.Event.CLICK_SKIP_GUIDEPAGE)
            is AnalyticsEvent.SplashDisplayed -> AnalyticsPayload(
                AnalyticsTracker.Event.SPLASH_DISPLAY,
                mapOf(AnalyticsTracker.Param.DURATION_TIME to event.durationMillis.coerceAtLeast(0L)),
            )
            is AnalyticsEvent.HomeEntered -> AnalyticsPayload(
                AnalyticsTracker.Event.ENTER_HOMEPAGE,
                mapOf(AnalyticsTracker.Param.REFERRER_NAME to event.referrer),
            )
            is AnalyticsEvent.CoreFeatureClicked -> AnalyticsPayload(
                AnalyticsTracker.Event.CLICK_CORE_FEATURES_BTN,
                mapOf(AnalyticsTracker.Param.DETAILS to event.feature.analyticsDetails()),
            )
            is AnalyticsEvent.CoreFeatureEntered -> AnalyticsPayload(
                AnalyticsTracker.Event.ENTER_CORE_FEATURES_PAGE,
                mapOf(AnalyticsTracker.Param.DETAILS to event.feature.analyticsDetails()),
            )
            is AnalyticsEvent.RatePopup -> AnalyticsPayload(
                AnalyticsTracker.Event.RATE_POPUP,
                mapOf(AnalyticsTracker.Param.IF_OK to event.accepted),
            )
            is AnalyticsEvent.FileManagerPopup -> AnalyticsPayload(
                AnalyticsTracker.Event.FILEMANAGER_POPUP,
                mapOf(AnalyticsTracker.Param.IF_OK to event.accepted),
            )
            is AnalyticsEvent.NotificationPopup -> AnalyticsPayload(
                AnalyticsTracker.Event.NOTIFICATION_POPUP,
                mapOf(
                    AnalyticsTracker.Param.NOTICE_FLAG to event.noticeFlag,
                    AnalyticsTracker.Param.IF_OK to event.accepted,
                ),
            )
            is AnalyticsEvent.TermsOpened -> AnalyticsPayload(
                AnalyticsTracker.Event.CHECK_TERMS,
                mapOf(AnalyticsTracker.Param.REFERRER_NAME to event.referrer),
            )
            is AnalyticsEvent.PrivacyOpened -> AnalyticsPayload(
                AnalyticsTracker.Event.CHECK_PRIVACY,
                mapOf(AnalyticsTracker.Param.REFERRER_NAME to event.referrer),
            )
            is AnalyticsEvent.FilePermissionResult -> AnalyticsPayload(
                if (event.accepted) AnalyticsTracker.Event.AGREE_FILEMANAGE_PERMISSION
                else AnalyticsTracker.Event.DISAGREE_FILEMANAGE_PERMISSION,
            )
            is AnalyticsEvent.NotificationPermissionResult -> AnalyticsPayload(
                if (event.accepted) AnalyticsTracker.Event.AGREE_PUSH_PERMISSION
                else AnalyticsTracker.Event.DISAGREE_PUSH_PERMISSION,
            )
            is AnalyticsEvent.FeatureOperation -> AnalyticsPayload(null, emptyMap())
        }

    private fun FeatureKey.analyticsDetails(): String? = when (this) {
        FeatureKey.JUNK_CLEAN -> "junkClean"
        FeatureKey.ANTI_VIRUS -> "virusAnti"
        FeatureKey.APP_LOCK -> "appLock"
        FeatureKey.DEVICE_INFO -> "deviceInfo"
        FeatureKey.BATTERY_INFO -> "batteryInfo"
        FeatureKey.APP_USAGE -> "appUsage"
        FeatureKey.NOTIFICATION_CLEANER -> "notificationClean"
        FeatureKey.WHATSAPP_CLEANER -> "whatsappClean"
        FeatureKey.NETWORK_USAGE -> "networkUsage"
        FeatureKey.NETWORK_SCAN -> "networkScan"
        FeatureKey.NETWORK_SPEED -> "networkSpeed"
        else -> if (group == com.aicleanerstorage.spaceclean.common.feature.FeatureGroup.FILES) "fileManage" else null
    }
}

data class AnalyticsPayload(
    val eventName: String?,
    val params: Map<String, Any?> = emptyMap(),
)
