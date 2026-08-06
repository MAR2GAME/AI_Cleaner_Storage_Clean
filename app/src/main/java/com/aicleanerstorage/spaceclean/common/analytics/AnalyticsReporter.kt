package com.aicleanerstorage.spaceclean.common.analytics

import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent

interface AnalyticsReporter {
    fun report(event: AnalyticsEvent)
}

sealed interface AnalyticsEvent {
    data object GuideScanningEntered : AnalyticsEvent
    data object GuideScanResultEntered : AnalyticsEvent
    data object GuideContinueClicked : AnalyticsEvent
    data object GuideSkipClicked : AnalyticsEvent
    data class SplashDisplayed(val durationMillis: Long) : AnalyticsEvent
    data class HomeEntered(val referrer: String?) : AnalyticsEvent
    data class CoreFeatureClicked(val feature: FeatureKey) : AnalyticsEvent
    data class CoreFeatureEntered(val feature: FeatureKey) : AnalyticsEvent
    data class RatePopup(val accepted: Boolean) : AnalyticsEvent
    data class FileManagerPopup(val accepted: Boolean) : AnalyticsEvent
    data class NotificationPopup(val noticeFlag: Int, val accepted: Boolean) : AnalyticsEvent
    data class TermsOpened(val referrer: String) : AnalyticsEvent
    data class PrivacyOpened(val referrer: String) : AnalyticsEvent
    data class FilePermissionResult(val accepted: Boolean) : AnalyticsEvent
    data class NotificationPermissionResult(val accepted: Boolean) : AnalyticsEvent
    data class FeatureOperation(val operation: FeatureOperationEvent) : AnalyticsEvent
}
