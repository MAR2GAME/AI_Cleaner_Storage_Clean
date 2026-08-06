package com.aicleanerstorage.spaceclean.app.analytics

import android.content.Intent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsTracker

class AppSessionAnalytics {
    fun onForeground() = AnalyticsTracker.onAppForeground()
    fun onBackground() = AnalyticsTracker.onAppBackground()
    fun handleLaunchIntent(intent: Intent?) = AnalyticsTracker.handleLaunchIntent(intent)
}
